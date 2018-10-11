package com.asuka.admin.controller.activiti;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.asuka.admin.annotation.SysLog;
import com.asuka.admin.entity.TaskData;
import com.asuka.admin.entity.TaskToInform;
import com.asuka.admin.entity.User;
import com.asuka.admin.entity.act.ActComment;
import com.asuka.admin.entity.act.LeaveApply;
import com.asuka.admin.entity.act.LeaveOpinion;
import com.asuka.admin.entity.act.RunningProcess;
import com.asuka.admin.realm.AuthRealm;
import com.asuka.admin.service.*;
import com.asuka.admin.util.CommonUtil;
import com.asuka.admin.util.LayerData;
import com.asuka.admin.util.RestResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 请假流程
 *
 * @Author: hex1n
 * @Date: 2018/10/5 23:43
 */
@Controller
@RequestMapping("/leave")
public class UserLeaveController {

    private Logger logger = LoggerFactory.getLogger(UserLeaveController.class);

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;


    @Resource
    private LeaveService leaveService;

    @Autowired
    ProcessEngine processEngine;

    @Resource
    private RoleService roleService;

    @Resource
    private UserService userService;

    @Resource
    private TaskInformService taskInformService;


    public static Map<String, Object> variables = Maps.newHashMap();

    public static List<TaskData> taskDataList = Lists.newArrayList();

    public static Map<String, Object> leaveOptionFlag = Maps.newHashMap();

    @GetMapping("/showLeaveList")
    @SysLog("跳转到请假流程列表")
    public String toShowLeaveList() {
        return "activiti/leave/leaveList";
    }

    @GetMapping("addLeave")
    @SysLog("跳转到新建请假申请")
    public String addLeave() {
        return "/activiti/leave/add-leave";
    }


    @PostMapping(value = "addLeave")
    @ResponseBody
    @SysLog("新建请假申请")
    public RestResponse addLeave(LeaveApply leaveApply) {

        AuthRealm.ShiroUser user = CommonUtil.getUser();
        String userId = user.getNickName();
        ArrayList<String> processDescList = new ArrayList<>();
        //获取所有流程定义
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().processDefinitionKey("leave").list();
        if (processDefinitions.size() > 0 && processDefinitions != null) {
            for (ProcessDefinition definition : processDefinitions) {
                if (definition.getDescription() != null) {
                    processDescList.add(definition.getDescription());
                }
            }
        }
        if (processDescList.size() > 0 && processDescList != null) {
            dataProcess(processDescList, userId);
        }
        // variables.put("applyUserId", userId);

        ProcessInstance processInstance = leaveService.startProcess(leaveApply, userId, variables);
        logger.info("流程id" + processInstance.getId() + "    已启动");
        String processInstanceId = processInstance.getId();
        //流程启动后就发送待办信息
        sendTaskInfo(processInstanceId);

        return RestResponse.success();
    }


    @PostMapping("/showLeaveList")
    @ResponseBody
    @SysLog("请假流程列表")
    public LayerData<LeaveApply> myLeaveProcess(@RequestParam(value = "page") int page,
                                                @RequestParam(value = "limit") int limit) {

        AuthRealm.ShiroUser currentUser = CommonUtil.getUser();
        String userId = currentUser.getNickName();
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        List<ProcessInstance> processInstances = processInstanceQuery.processDefinitionKey("leave").listPage(limit * (page - 1), limit);
        List<LeaveApply> leaveApplys = new ArrayList<>();
        if (processInstances.size() > 0 && processInstances != null) {
            for (ProcessInstance instance : processInstances) {

                LeaveApply leave = leaveService.getLeaveByBusinessKey(Integer.parseInt(instance.getBusinessKey()));
                if (leave != null) {
                    if (leave.getUserId().equals(userId)) {
                        leaveApplys.add(leave);
                    } else {
                        continue;
                    }
                }
            }
        }

        LayerData<LeaveApply> layerData = new LayerData<>();
        layerData.setData(leaveApplys);
        layerData.setCount(leaveApplys.size());

        return layerData;
    }

    /**
     * 只读图片页面
     */
    @GetMapping("shinePics/{processInstanceId}")
    public String shinePics(Model model, @PathVariable String processInstanceId) {
        model.addAttribute("processInstanceId", processInstanceId);
        return "/activiti/leave/shinePics";
    }

    @GetMapping("getShineProcImage")
    @ResponseBody
    @SysLog("流程图跟踪")
    public String getShineProcImage(@RequestParam("id") int id, HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String processInstanceId = String.valueOf(id);

        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String procDefId = processInstance.getProcessDefinitionId();
        InputStream imageStream = null;
        // 当前活动节点、活动线
        List<String> activeActivityIds = new ArrayList<>(), highLightedFlows;
        //所有的历史活动节点
        List<String> highLightedFinishes = new ArrayList<>();

        // 如果流程已经结束，则得到结束节点
        if (!isFinished(processInstanceId)) {
            // 如果流程没有结束，则取当前活动节点
            // 根据流程实例ID获得当前处于活动状态的ActivityId合集
            activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
        }

        // 获得历史活动记录实体（通过启动时间正序排序，不然有的线可以绘制不出来）
        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc().list();

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            highLightedFinishes.add(historicActivityInstance.getActivityId());
        }
        // 计算活动线
        highLightedFlows = getHighLightedFlows(
                (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                        .getDeployedProcessDefinition(procDefId),
                historicActivityInstances);

        if (null != activeActivityIds) {
            try {
                response.setContentType("image/png");

                // 根据流程定义ID获得BpmnModel
                BpmnModel bpmnModel = repositoryService
                        .getBpmnModel(procDefId);
                // 输出资源内容到相应对象
                imageStream = new DefaultProcessDiagramGenerator().generateDiagram(
                        bpmnModel,
                        "png",
                        activeActivityIds,
                        highLightedFlows,
                        "宋体",
                        "宋体",
                        "宋体",
                        null,
                        1.0);

                int len;
                byte[] b = new byte[1024];

                while ((len = imageStream.read(b, 0, 1024)) != -1) {
                    response.getOutputStream().write(b, 0, len);
                }
            } finally {
                if (imageStream != null) {
                    imageStream.close();
                }
            }
        }
        return processInstanceId;
    }

    public boolean isFinished(String processInstanceId) {
        return historyService.createHistoricProcessInstanceQuery().finished().processInstanceId(processInstanceId).count() > 0;
    }

    public List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinitionEntity, List<HistoricActivityInstance> historicActivityInstances) {

        // 用以保存高亮的线flowId
        List<String> highFlows = new ArrayList<>();
        List<String> highActivitiImpl = new ArrayList<>();

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            highActivitiImpl.add(historicActivityInstance.getActivityId());
        }

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            ActivityImpl activityImpl = processDefinitionEntity.findActivity(historicActivityInstance.getActivityId());
            List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();
            // 对所有的线进行遍历
            for (PvmTransition pvmTransition : pvmTransitions) {
                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition.getDestination();
                if (highActivitiImpl.contains(pvmActivityImpl.getId())) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }

        return highFlows;
    }


    /**
     * 处理流程描述数据
     *
     * @param list
     */
    public void dataProcess(List<String> list, String currentUser) {

        if (list.size() > 0 && list != null) {
            for (String s : list) {
                taskDataList = JSONArray.parseArray(s, TaskData.class);
            }
        }
        for (TaskData taskData : taskDataList) {

            if (taskData.getPType().equals("部门经理")) {
                StringBuilder sb = new StringBuilder();
                List<User> users = roleService.getUserByRoleName(taskData.getPType());
                if (users.size() > 0 && users != null) {
                    for (User user : users) {
                        sb.append(user.getNickName()).append(",");
                    }
                }
                String tlUser = sb.toString().substring(0, sb.length() - 1);

                variables.put(taskData.getPKey(), tlUser);
            } else if (taskData.getPType().equals("人事")) {
                StringBuilder sb = new StringBuilder();
                List<User> hrUsers = roleService.getUserByRoleName(taskData.getPType());
                if (hrUsers.size() > 0 && hrUsers != null) {
                    for (User user : hrUsers) {
                        sb.append(user.getNickName()).append(",");
                    }
                }
                String hrUser = sb.substring(0, sb.length() - 1);
                variables.put(taskData.getPKey(), hrUser);
            } else if (taskData.getPType().equals("申请人")) {
                variables.put(taskData.getPKey(), currentUser);
            }

        }

    }


    @GetMapping("/todoTasks")
    @SysLog("跳转到待办任务")
    public String showTask() {

        return "/activiti/task/taskList";
    }

    @GetMapping("/showTodoTasks")
    @SysLog("我的待办任务")
    @ResponseBody
    public LayerData<LeaveApply> myTodoTasks(@RequestParam(value = "page") int page,
                                             @RequestParam(value = "limit") int limit) {


        LayerData<LeaveApply> layerData = new LayerData<>();

        AuthRealm.ShiroUser currentUser = CommonUtil.getUser();

        List<LeaveApply> leaveApplies = taskInformService.findTasksByUserId(currentUser.getId());
        if (leaveApplies.size() > 0 && leaveApplies != null) {

            layerData.setData(leaveApplies);
            layerData.setCount(leaveApplies.size());
            return layerData;
        }

        return layerData;
    }

    @GetMapping("/agent")
    @SysLog("到任务详情页")
    public ModelAndView doTask(String processInstanceId) {

        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        Map map = Maps.newHashMap();


        LeaveApply leaveApply = leaveService.findLeaveByProcessInstanceId(processInstanceId);

        map.put("taskId", task.getId());
        map.put("leave", leaveApply);
        map.put("processInstanceId", processInstanceId);


        return new ModelAndView("/activiti/task/task-agent", map);
    }


    @PostMapping("/complete")
    @SysLog("办理任务")
    public RestResponse completeTask(LeaveOpinion leaveOpinion) {


        AuthRealm.ShiroUser user = CommonUtil.getUser();

        leaveOpinion.setCreateTime(new Date());
        leaveOpinion.setOpName(user.getNickName());
        leaveOpinion.setOpId(String.valueOf(user.getId()));

        List<String> roleNames = roleService.getRoleByUserId(user.getId());
        if (roleNames.size() > 0 && roleNames != null) {
            for (String roleName : roleNames) {
                if (roleName.equals("部门经理")) {
                    variables.put("tlcondition", leaveOpinion.isFlag());
                } else if (roleName.equals("人事")) {
                    variables.put("hrcondition", leaveOpinion.isFlag());
                } else {
                    variables.put("reApply", leaveOpinion.isFlag());
                }
            }
        }

        leaveOptionFlag.put(leaveOpinion.getTaskId(), leaveOpinion.isFlag());

        Authentication.setAuthenticatedUserId(user.nickName);
        taskService.addComment(leaveOpinion.getTaskId(), leaveOpinion.getProcessInstanceId(), leaveOpinion.getOpinion());
        //签收/办理任务
        taskService.claim(leaveOpinion.getTaskId(), user.getNickName());
        taskService.complete(leaveOpinion.getTaskId(), variables);

        taskInformService.deleteByProcessInstanceId(leaveOpinion.getProcessInstanceId());

        //发送待办信息
        sendTaskInfo(leaveOpinion.getProcessInstanceId());

        return RestResponse.success();
    }

    @GetMapping("/leaveDetail")
    @SysLog("任务详情")
    public String leaveDetail(Model model, String processId) {

        List<ActComment> list = Lists.newArrayList();

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processId).singleResult();

        Set<Map.Entry<String, Object>> entries = leaveOptionFlag.entrySet();

        //保证流程在运行

        if (processInstance != null) {

            List<HistoricActivityInstance> instances = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).activityType("userTask").list();

            for (HistoricActivityInstance instance : instances) {
                String taskId = instance.getTaskId();
                List<Comment> taskComments = taskService.getTaskComments(taskId);
                for (Comment taskComment : taskComments) {
                    for (Map.Entry<String, Object> entry : entries) {
                        if (entry.getKey().equals(taskId)) {
                            ActComment actComment = new ActComment();
                            actComment.setCName(taskComment.getUserId());
                            actComment.setCreated(taskComment.getTime());
                            actComment.setFlag((Boolean) entry.getValue());
                            actComment.setMessage(taskComment.getFullMessage());
                            actComment.setTaskId(taskId);
                            list.add(actComment);
                        }
                    }
                }
            }
        }

        model.addAttribute("leaveDetail", JSON.toJSONString(list));
        return "/activiti/leave/leaveDetail";
    }

    @GetMapping("myProcess")
    @SysLog("跳转到我参与的流程列表")
    public ModelAndView myParticipateProcess() {

        return new ModelAndView("/activiti/participate/list");
    }

    @PostMapping("/myParticipateProcess")
    @ResponseBody
    @SysLog("参与正在运行的流程")
    public LayerData<RunningProcess> allExecution(@RequestParam(value = "page") int page,
                                                  @RequestParam(value = "limit") int limit) {

        AuthRealm.ShiroUser currentUser = CommonUtil.getUser();
        String userId = currentUser.getNickName();

        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        List<ProcessInstance> processInstances = processInstanceQuery.processDefinitionKey("leave").involvedUser(userId).listPage((page - 1) * limit, limit);
        List<RunningProcess> runningProcessList = Lists.newArrayList();
        for (ProcessInstance instance : processInstances) {
            RunningProcess process = new RunningProcess();
            process.setActivityId(instance.getActivityId());
            process.setBusinessKey(instance.getBusinessKey());
            process.setExecutionId(instance.getId());
            process.setProcessInstanceId(instance.getProcessInstanceId());
            runningProcessList.add(process);
        }

        LayerData<RunningProcess> layerData = new LayerData<>();
        layerData.setCount(runningProcessList.size());
        layerData.setData(runningProcessList);
        return layerData;
    }

    /**
     * @param processInstanceId
     */
    public void sendTaskInfo(String processInstanceId) {
        //获取所有任务节点封装到集合中
        List<String> userTasks = Lists.newArrayList();

        Set<Map.Entry<String, Object>> entries = variables.entrySet();

        //根据任务id获取当前任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        //根据当前任务获取当前流程的流程定义,然后根据流程定义获取所有节点
        for (Task task : taskList) {
            ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                    .getDeployedProcessDefinition(task.getProcessDefinitionId());

            List<ActivityImpl> activityList = def.getActivities();
            //根据当前任务获取当前流程的执行id,执行实例,以及当前流程节点的ID
            String executionId = task.getExecutionId();
            ExecutionEntity executionEntity = (ExecutionEntity) runtimeService.
                    createExecutionQuery().executionId(executionId).singleResult();

            //当前流程的id
            String activityId = executionEntity.getActivityId();

            //当前任务节点名称
            String currentTaskName = task.getName();

            for (ActivityImpl activity : activityList) {

                if ("userTask".equals(activity.getProperty("type"))) {
                    userTasks.add((String) activity.getProperty("name"));
                }
            }
            //遍历所有任务节点
            Fool:
            for (TaskData taskData : taskDataList) {

                if (currentTaskName.equals(taskData.getPTaskName())) {
                    for (Object userTask : userTasks) {
                        if (currentTaskName.equals(userTask)) {

                            for (Map.Entry<String, Object> entry : entries) {
                                if (entry.getKey().equals(taskData.getPKey())) {
                                    String[] split = entry.getValue().toString().split(",");
                                    for (String s : split) {
                                        List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned(s).list();
                                        for (Task task1 : tasks) {
                                            //插入待办信息
                                            List<User> users = userService.findUserByNickName(s);
                                            for (User user : users) {
                                                TaskToInform taskToInform = new TaskToInform();
                                                taskToInform.setTaskInfo(task1.getName());
                                                taskToInform.setCreateDate(task1.getCreateTime());
                                                taskToInform.setProcessInstanceId(task1.getProcessInstanceId());
                                                taskToInform.setUserId(user.getId());
                                                taskToInform.setUserNickName(user.getNickName());
                                                taskInformService.add(taskToInform);
                                            }

                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

}
