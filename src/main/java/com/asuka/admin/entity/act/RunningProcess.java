package com.asuka.admin.entity.act;

import lombok.Data;

/**
 * @Author: hex1n
 * @Date: 2018/10/7 14:36
 */
@Data
public class RunningProcess {

    private String executionId;
    private String processInstanceId;
    private String businessKey;
    private String activityId;
}
