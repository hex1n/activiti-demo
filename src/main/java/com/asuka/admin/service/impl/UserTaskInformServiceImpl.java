package com.asuka.admin.service.impl;

import com.asuka.admin.dao.TaskInformDao;
import com.asuka.admin.entity.TaskToInform;
import com.asuka.admin.entity.act.LeaveApply;
import com.asuka.admin.service.TaskInformService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: hex1n
 * @Date: 2018/10/8 11:14
 */
@Service
public class UserTaskInformServiceImpl implements TaskInformService {

    @Resource
    public TaskInformDao userTaskInformDao;

    @Override
    public void add(TaskToInform taskToInform) {
        userTaskInformDao.add(taskToInform);
    }

    @Override
    public void delByUserId(int id) {

        userTaskInformDao.delByUserId(id);
    }

    @Override
    public List<LeaveApply> findTasksByUserId(Long id) {


        return userTaskInformDao.findTasksByUserId(id);
    }

    @Override
    public void deleteByProcessInstanceId(String processInstanceId) {

        userTaskInformDao.deleteByProcessInstanceId(processInstanceId);
    }
}
