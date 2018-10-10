package com.asuka.admin.service;

import com.asuka.admin.entity.TaskToInform;
import com.asuka.admin.entity.act.LeaveApply;

import java.util.List;

/**
 * @Author: hex1n
 * @Date: 2018/10/8 11:13
 */
public interface TaskInformService {


    void add(TaskToInform taskToInform);

    void delByUserId(int id);

    List<LeaveApply> findTasksByUserId(Long id);

    void deleteByProcessInstanceId(String processInstanceId);
}
