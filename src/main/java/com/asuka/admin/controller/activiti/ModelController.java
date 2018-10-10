package com.asuka.admin.controller.activiti;

import com.asuka.admin.annotation.SysLog;
import com.asuka.admin.entity.act.ActModel;
import com.asuka.admin.util.LayerData;
import com.asuka.admin.util.RestResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程模型列表
 *
 * @Author: hex1n
 * @Date: 2018/10/5 10:37
 */
@Controller
@RequestMapping("/act")
public class ModelController {

    private Logger logger = LoggerFactory.getLogger(ModelController.class);

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

    @Resource
    private ObjectMapper objectMapper;

    /**
     * @param request
     * @param response
     */
    @RequestMapping("/design-process")
    @SysLog("流程设计器,新建流程")
    public void create(HttpServletRequest request, HttpServletResponse response) {
        try {
            //初始化一个空模型
            Model model = repositoryService.newModel();
            //设置默认信息
            String name = "new-process";
            String description = "";
            int revision = 1;
            String key = "process";

            ObjectNode modelNode = objectMapper.createObjectNode();
            modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
            modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);

            model.setName(name);
            model.setKey(key);
            model.setMetaInfo(modelNode.toString());

            repositoryService.saveModel(model);
            String id = model.getId();
            //完善ModelEditorSource
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id", "canvas");
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace",
                    "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.put("stencilset", stencilSetNode);
            repositoryService.addModelEditorSource(id, editorNode.toString().getBytes("utf-8"));
            response.sendRedirect(request.getContextPath() + "/static/modeler.html?modelId=" + id);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("创建模型失败");
        }
    }

    @GetMapping("/process-modelList")
    @SysLog("跳转流程模型列表")
    public String toDeployedProcess() {

        return "activiti/model/list";
    }

    @PostMapping("/process-modelList")
    @ResponseBody
    @SysLog("模型列表")
    public LayerData modelList(@RequestParam(value = "page") int page,
                               @RequestParam(value = "limit") int limit,
                               ActModel actModel) {

        ModelQuery modelQuery = repositoryService.createModelQuery();

        if (actModel != null) {

            if (!StringUtils.isEmpty(actModel.getKey())) {
                modelQuery.modelKey(actModel.getKey());
            }
            if (!StringUtils.isEmpty(actModel.getName())) {
                modelQuery.modelNameLike("%" + actModel.getName() + "%");
            }
        }
        List<Model> models = modelQuery.listPage(limit * (page - 1), limit);

        long count = repositoryService.createModelQuery().count();

        List<ActModel> actModels = new ArrayList<>();

        models.forEach(model -> actModels.add(new ActModel(model)));

        LayerData<ActModel> layerData = new LayerData<>();

        layerData.setCount((int) count);
        layerData.setData(actModels);

        return layerData;
    }

    @PostMapping(value = "/deployment")
    @ResponseBody
    @SysLog("流程模型发布")
    public RestResponse deployment(String id) {

        try {
            Model modelData = repositoryService.getModel(id);
            byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());

            if (bytes == null) {
                return RestResponse.failure("模型数据为空,请先设计流程并成功保存，再进行发布.");
            }
            JsonNode modelNode = null;
            modelNode = new ObjectMapper().readTree(bytes);
            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            if (model.getProcesses().size() == 0) {
                return RestResponse.failure("数据模型不符要求，请至少设计一条主线流程。");
            }
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model, "utf-8");

            //发布流程
            String processName = modelData.getName() + ".bpmn20.xml";

            Deployment deployment = repositoryService.createDeployment()
                    .name(modelData.getName())
                    .addString(processName, new String(bpmnBytes))
                    .deploy();
            modelData.setDeploymentId(deployment.getId());
            repositoryService.saveModel(modelData);
            return RestResponse.success();
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.failure("发布失败");
        }
    }

    @PostMapping("/delModel")
    @ResponseBody
    @SysLog("删除流程模型")
    public RestResponse delModel(String id) {
        try {
            repositoryService.deleteModel(id);
            return RestResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.failure("删除失败");
        }
    }

    @PostMapping("/delModels")
    @ResponseBody
    @SysLog("批量删除流程模型")
    public RestResponse delModels(@RequestBody List<ActModel> actModels) {

        if (actModels == null || actModels.size() == 0) {
            return RestResponse.failure("获取数据失败");
        }

        try {

            for (ActModel actModel : actModels) {
                repositoryService.deleteModel(actModel.getId());
            }
            return RestResponse.success();
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.failure("批量模型删除失败");
        }
    }
}
