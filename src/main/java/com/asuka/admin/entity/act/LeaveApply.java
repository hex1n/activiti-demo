package com.asuka.admin.entity.act;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: hex1n
 * @Date: 2018/10/7 11:04
 */
@Data
public class LeaveApply implements Serializable {

    private int id;
    private String processInstanceId;
    private String userId;
    private String beginTime;
    private String endTime;
    private String reason;
    private int days;

}
