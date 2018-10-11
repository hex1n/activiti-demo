package com.asuka.admin.controller.activiti;

import com.asuka.admin.annotation.SysLog;
import com.asuka.admin.entity.act.ActProcessDefinition;
import com.asuka.admin.util.LayerData;
import com.asuka.admin.util.RestResponse;
import org.activiti.engine.*;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * @Author: hex1n
 * @Date: 2018/10/4 14:33
 */
@Controller
@RequestMapping("/act")
public class ActivitiController {

    private Logger logger = LoggerFactory.getLogger(ActivitiController.class);

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private FormService formService;
    @Resource
    private IdentityService identityService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;


    @GetMapping("/deployed-process")
    @SysLog("跳转已部署流程列表")
    public String toDeployedProcess() {

        return "activiti/deployed/list";
    }

    /**
     * @return
     */
    @PostMapping("/deployed-process")
    @ResponseBody
    @SysLog("已部署流程列表")
    public LayerData<ActProcessDefinition> deployedProcess(@RequestParam(value = "page") int page,
                                                           @RequestParam(value = "limit") int limit,
                                                           ActProcessDefinition definition) {

        List<ProcessDefinition> processDefinitions = null;
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        if (definition != null) {

            if (!StringUtils.isEmpty(definition.getDeploymentId())) {
                processDefinitionQuery.deploymentId(definition.getDeploymentId());
            }
            if (!StringUtils.isEmpty(definition.getName())) {
                processDefinitionQuery.processDefinitionNameLike("%" + definition.getName() + "%");
            }
        }
        processDefinitions = processDefinitionQuery.listPage(limit * (page - 1), limit);

        long count = repositoryService.createDeploymentQuery().count();

        List<ActProcessDefinition> actProcessDefinitions = new ArrayList<>();

        processDefinitions.forEach(processDefinition -> actProcessDefinitions.add(new ActProcessDefinition(processDefinition)));

        LayerData<ActProcessDefinition> layerData = new LayerData<>();

        layerData.setCount((int) count);
        layerData.setData(actProcessDefinitions);

        return layerData;
    }

    /**
     * @param id           流程定义id
     * @param resourceName 资源名称
     * @param response     http响应
     */
    @RequestMapping("read-resource")
    @SysLog(" 读取流程资源")
    public String readResource(@RequestParam("id") String id,
                               @RequestParam("resourceName") String resourceName, HttpServletResponse response) throws IOException {

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        ProcessDefinition processDefinition = processDefinitionQuery.processDefinitionId(id).singleResult();

        //通过接口读取
        InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);

        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.copy(resourceAsStream, outputStream);

        return null;

    }

    /**
     * @param id 流程部署id
     * @return
     */
    @PostMapping("/delDeploy")
    @ResponseBody
    @SysLog("级联删除流程定义(单个)")
    public RestResponse deleteDeploy(@RequestParam("id") String id) {

        if (id == null) {
            return RestResponse.failure("参数错误");
        }
        try {
            if (id.equals("1")) {
                return RestResponse.failure("通用请假流程不能删除");
            }
            repositoryService.deleteDeployment(id, true);
            return RestResponse.success();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("删除失败");
            return RestResponse.failure("删除失败");
        }
    }

    @PostMapping("/delDeployeds")
    @ResponseBody
    @SysLog("批量删除流程定义)")
    public RestResponse deleteDeploys(@RequestBody List<ActProcessDefinition> processDefinitions) {

        if (processDefinitions == null || processDefinitions.size() == 0) {

            return RestResponse.failure("请选择需要删除的流程定义");
        }
        try {
            for (ActProcessDefinition processDefinition : processDefinitions) {
                if (processDefinition.getDeploymentId().equals("1")) {
                    return RestResponse.failure("通用请假流程不能删除");
                }
                repositoryService.deleteDeployment(processDefinition.getDeploymentId(), true);
            }
            return RestResponse.success();
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.failure("批量删除失败");
        }
    }

    @PostMapping("/upload-process")
    @ResponseBody
    @SysLog("流程文件上传")
    public RestResponse fileUpload(@RequestParam MultipartFile file) {

        if (file == null) {
            logger.info("获取数据失败");
            return RestResponse.failure("获取数据失败");
        }
        //获取上传的文件名
        String filename = file.getOriginalFilename();

        try {
            InputStream fileInputStream = file.getInputStream();
            String extension = FilenameUtils.getExtension(filename);
            //如果是zip或者bar类型的文件用ZipInputStream方式部署
            DeploymentBuilder deployment = repositoryService.createDeployment();
            if ("zip".equals(extension) || "bar".equals(extension)) {
                ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
                deployment.addZipInputStream(zipInputStream);
            } else {
                //其他类型的文件直接部署
                deployment.addInputStream(filename, fileInputStream);
            }
            deployment.deploy();
            return RestResponse.success();
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("上传流程失败");
            return RestResponse.failure("上传流程失败");
        }
    }


}
