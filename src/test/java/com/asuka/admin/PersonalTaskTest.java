package com.asuka.admin;

import com.google.common.collect.Maps;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * 个人任务
 *
 * @Author: hex1n
 * @Date: 2018/9/14 15:15
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PersonalTaskTest {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    IdentityService identityService;

    public static Map<String, Object> variables = new HashMap();


    /**
     * 部署流程
     *
     * @return
     */
    @Test
    public void deploymentProcess() {

        //根据bpmn文件部署流程
        Deployment deployment = repositoryService.createDeployment()
                .name("请假流程--")
                .addClasspathResource("processes/leave.bpmn20.xml")
                .deploy();

        assertNotNull(deployment);

        System.out.println("部署ID  :  " + deployment.getId());
        System.out.println("部署名称: " + deployment.getName());


    }

    @Test
    public void test11() {
        int i = 0;
        while (i < 21) {
            deploymentProcess();
            i++;
        }
    }


    @Test
    public void test99() {
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().list();
        if (processDefinitionList.size() > 0 && processDefinitionList != null) {
            for (ProcessDefinition processDefinition : processDefinitionList) {

                System.out.println(processDefinition + "......");
            }
        }
    }

    /**
     * 启动流程
     */
    @Test
    public void startProcess() {

        //定义流程定义的key
        String processDefinitionKey = "leave";

        //定义经理审批办理人
      /*  ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey).singleResult();
        String description = processDefinition.getDescription();
        System.out.println(description);*/
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey).list();
        for (ProcessDefinition processDefinition : processDefinitions) {
            String description = processDefinition.getDescription();
            System.out.println(description);
        }

        HashMap<String, Object> variables = Maps.newHashMap();
        identityService.setAuthenticatedUserId("李四");
        variables.put("tl", "1111");


//        String[] splits = description.split(",");
//
//        System.out.println(splits);
//        for (String split : splits) {
//            String[] split1 = split.split(":");
//
//            variables.put(split1[0], split1[0]);
//        }


        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);
        assertNotNull(processInstance);

        System.out.println("流程实例id:  " + processInstance.getId() + "         流程定义id:  " + processInstance.getProcessDefinitionId());

    }


    /**
     * 查询个人待办任务
     *
     * @return
     */
    @Test
    public void queryPersonalTask() {

        String assignee = "王五";
        List<Task> taskList = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();
        if (taskList.size() > 0 && taskList != null) {
            for (Task task : taskList) {
                System.out.println("任务ID:  " + task.getId());
                System.out.println("任务名称:   " + task.getName());
                System.out.println("任务办理人:  " + task.getAssignee());
                System.out.println("任务创建时间: " + task.getCreateTime());
                System.out.println("流程定义ID: " + task.getProcessDefinitionId());
                System.out.println("流程实例ID: " + task.getProcessInstanceId());
                System.out.println("执行对象ID: " + task.getExecutionId());
                System.out.println("==============================================");
            }
        } else {
            System.out.println("没有查询到任务");
        }
    }

    /**
     * 查询候选人任务
     */
    @Test
    public void find() {
        String candidateUser = "t2--02";
        List<Task> taskList = taskService.createTaskQuery()
                .taskCandidateUser(candidateUser)
                .list();
        if (taskList.size() > 0 && taskList != null) {

            for (Task task : taskList) {
                System.out.println("任务ID:  " + task.getId());
                System.out.println("任务名称:   " + task.getName());
                System.out.println("任务办理人:  " + task.getAssignee());
                System.out.println("任务创建时间: " + task.getCreateTime());
                System.out.println("流程定义ID: " + task.getProcessDefinitionId());
                System.out.println("流程实例ID: " + task.getProcessInstanceId());
                System.out.println("执行对象ID: " + task.getExecutionId());
                System.out.println("==============================================");
            }
        } else {
            System.out.println("没查询到任务");
        }
    }

    /**
     * 签收任务
     */
    @Test
    public void claimTask() {

        //任务id
        String taskId = "325012";
        //办理人
        String userId = "张三";
        taskService.claim(taskId, userId);
        System.out.println(userId + "  签收了组任务");

    }


    /**
     * 办理个人业务
     *
     * @return
     */
    @Test
    public void dealPersonalTask() {

        //定义任务ID
        String taskId = "310012";
        String processDefinitionKey = "leave";
        //定义人事办理人
//        String hrName = "张三";
//        Map<String, Object> variables = new HashMap<>();
//        variables.put("hr", hrName);
//        HashMap<String, Object> variables = Maps.newHashMap();
//        String tlName = "张三";
//        String hrName = "李四";
//        variables.put("tl", tlName);
//        variables.put("hr", hrName);
//        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
//                .processDefinitionKey(processDefinitionKey).singleResult();
//        String description = processDefinition.getDescription();
//        System.out.println(description);

//        HashMap<String, Object> variables = Maps.newHashMap();

//        String[] splits = description.split(",");

//        System.out.println(splits);
//        for (String split : splits) {
//            String[] split1 = split.split(":");
//
//            variables.put(split1[0], split1[1]);
//        }
//        variables.put("deptLeaderApproved","true");
        variables.put("hrApprove", "true");
        taskService.complete(taskId, variables);
    }

    /**
     * 查询流程历史活动
     *
     * @return
     */
    @Test
    public void findHisActivitiList() {
        //定义流程实例ID
        String processInstanceId = "392505";
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        if (list.size() > 0 && list != null) {
            for (HistoricActivityInstance instance : list) {
                System.out.println(instance.getId() + "  " + instance.getActivityName() + "   " + instance.getAssignee());
                System.out.println("==========================");
            }
        } else {
            System.out.println("没有查询到流程历史活动");
        }
    }

    /**
     * 查看用户历史任务
     *
     * @return
     */
    @Test
    public void queryHistoryTask() {

        //定义用户
        String assignee = "张三";

        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(assignee)
                .list();
        if (list.size() > 0 && list != null) {
            for (HistoricTaskInstance instance : list) {
                System.out.println(instance.getStartTime() + "\n" + instance.getEndTime() + "\n" + instance.getDurationInMillis());
                System.out.println("任务ID：" + instance.getId());
                System.out.println("任务名称：" + instance.getName());
                System.out.println("流程实例ID：" + instance.getProcessInstanceId());
                System.out.println("流程定义ID：" + instance.getProcessDefinitionId());
                System.out.println("执行对象ID：" + instance.getExecutionId());
                System.out.println("任务办理人：" + instance.getAssignee());
                System.out.println("==========================================================");
            }
        } else {
            System.out.println("没有查询结果");
        }
    }

    /**
     * 删除流程
     */
    @Test
    public void deleteDeployProcess() {

        //定义流程id
        List<Deployment> list = repositoryService.createNativeDeploymentQuery().sql("select*from act_re_deployment").list();
        if (list.size() > 0 && list != null) {

            for (Deployment deployment : list) {
                repositoryService.deleteDeployment(deployment.getId(), true);
            }
//            String deploymentId = "470001";

        }

    }

    @Test
    public void findUserByGroup() {

        int i = 1;

        List<User> users = identityService.createUserQuery().memberOfGroup("t" + i).list();
        System.out.println(users.size());
    }

    /**
     * 获取当前节点以及下一步路径或节点
     */
    @Test
    public void test001() {

        String processInstanceId = "112501";

        //根据任务id获取当前任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();

        //根据当前任务获取当前流程的流程定义,然后根据流程定义获得所有的节点

        for (Task task : taskList) {
            ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                    .getDeployedProcessDefinition(task.getProcessDefinitionId());

            List<ActivityImpl> activityList = def.getActivities();

            //根据当前任务获取当前流程的执行ID,执行实例,以及当前流程节点的ID
            String excId = task.getExecutionId();
            ExecutionEntity executionEntity = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(excId).singleResult();

            String activitiId = executionEntity.getActivityId();


            //循环activitiList并判断当前流程所处节点,然后得到当前节点实例,根据节点实例获取所有从当前节点触发的路径.
            //然后根据路径获得下一个节点实例
            for (ActivityImpl activity : activityList) {
                String id = activity.getId();
                if (activitiId.equals(id)) {
                    //输出某个节点的某种属性
                    System.out.println("当前任务:" + activity.getProperty("name"));
                    //获取从某个节点出来的所有线路
                    // List<PvmTransition> outgoingTransitions = activity.getOutgoingTransitions();
                    // for (PvmTransition transition : outgoingTransitions) {
                    //     //获得线路的终节点
                    //     PvmActivity destination = transition.getDestination();
                    //     System.out.println("下一步任务:" + destination.getProperty("name") + "====" + destination.getProperty("type"));
                    // }
                } else if ("userTask".equals(activity.getProperty("type"))) {


                    System.out.println("下一步任务:" + activity.getProperty("name"));

                    break;
                }
            }
        }
    }


}
