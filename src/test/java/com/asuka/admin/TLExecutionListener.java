package com.asuka.admin;

import com.asuka.admin.service.UserService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: hex1n
 * @Date: 2018/10/9 9:02
 */
public class TLExecutionListener implements ExecutionListener, TaskListener {


    @Override
    public void notify(DelegateExecution execution) throws Exception {

    }

    @Autowired
    private UserService userService;

    @Override
    public void notify(DelegateTask delegateTask) {

    }
}
