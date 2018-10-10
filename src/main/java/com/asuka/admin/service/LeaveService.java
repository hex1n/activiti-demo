package com.asuka.admin.service;

import com.asuka.admin.entity.act.LeaveApply;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.List;
import java.util.Map;

/**
 * @Author: hex1n
 * @Date: 2018/10/7 11:21
 */
public interface LeaveService {

    ProcessInstance startProcess(LeaveApply leaveApply, String userId, Map<String,Object> variables);

    List<LeaveApply> selectListByPage(LeaveApply leaveApply);

    LeaveApply getLeaveByBusinessKey(int businessKey);

    LeaveApply findLeaveByProcessInstanceId(String processInstanceId);
}
