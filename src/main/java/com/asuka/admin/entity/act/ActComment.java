package com.asuka.admin.entity.act;

import lombok.Data;
import org.activiti.engine.task.Comment;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程审批批注
 *
 * @Author: hex1n
 * @Date: 2018/10/11 9:42
 */
@Data
public class ActComment implements Serializable {


    /**
     * 任务编码
     */
    private String taskId;
    /**
     * 审批人/审批部门
     */
    private String cName;

    /**
     * 审批时间
     */
    private Date created;

    /**
     * 审批备注信息
     */
    private String message;

    /**
     * 审批同意还是驳回
     */
    private boolean flag;

    public ActComment(Comment comment) {

        this.cName = comment.getUserId();
        this.created = comment.getTime();
        this.message = comment.getFullMessage();
    }

    public ActComment() {
    }


}
