package com.asuka.admin.entity.act;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程日志表
 *
 * @Author: hex1n
 * @Date: 2018/10/13 9:35
 */
@Data
public class ActTaskLog implements Serializable {


    private static final long serialVersionUID = 2608115838356606912L;

    private int id;

    /**
     * 业务id
     */
    private String busId;
    /**
     * 流程定义id
     */
    // private String defId;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务办理人
     */
    private String transactor;

    /**
     * 任务办理人id
     */
    private String transactorId;
    /**
     * 任务办理时间
     */
    private Date createTime;
    /**
     * 审批意见
     */
    private String appOpinion;
    /**
     * 审批行为: 1==同意  , 2==反对
     */
    private String appAction;


}
