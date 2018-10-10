package com.asuka.admin.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务通知表
 *
 * @Author: hex1n
 * @Date: 2018/10/8 11:11
 */
@Data
public class TaskToInform implements Serializable {

    private int id;
    private long userId;
    private String taskInfo;
    private Date createDate;
    private String userNickName;
    private String processInstanceId;
}
