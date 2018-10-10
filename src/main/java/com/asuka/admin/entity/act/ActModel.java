package com.asuka.admin.entity.act;

import lombok.Getter;
import lombok.Setter;
import org.activiti.engine.repository.Model;

import java.text.SimpleDateFormat;

/**
 * 流程模型列表
 *
 * @Author: hex1n
 * @Date: 2018/10/5 10:54
 */
@Getter
@Setter
public class ActModel {

    private String id;
    private String name;
    private String key;
    private String category;
    private String createTime;
    private String lastUpdateTime;
    private Integer version;
    private String metaInfo;
    private String deploymentId;
    private String tenantId;
    private boolean hasEditorSource;

    public ActModel() {
    }


    public ActModel(Model model) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.id = model.getId();
        this.name = model.getName();
        this.key = model.getKey();
        this.category = model.getCategory();
        this.createTime = sdf.format(model.getCreateTime());
        this.lastUpdateTime = sdf.format(model.getLastUpdateTime());
        this.version = model.getVersion();
        this.metaInfo = model.getMetaInfo();
        this.deploymentId = model.getDeploymentId();
        this.tenantId = model.getTenantId();
        this.hasEditorSource = model.hasEditorSource();
    }
}
