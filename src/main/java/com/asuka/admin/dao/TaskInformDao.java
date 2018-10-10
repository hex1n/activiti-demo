package com.asuka.admin.dao;

import com.asuka.admin.entity.TaskToInform;
import com.asuka.admin.entity.act.LeaveApply;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: hex1n
 * @Date: 2018/10/8 11:15
 */
@Mapper
public interface TaskInformDao extends BaseMapper<TaskToInform> {

//    void add(TaskToInform taskToInform);

    void delByUserId(long userId);

    TaskToInform getByUserId(long userId);

    void add(TaskToInform taskToInform);

    List<LeaveApply> findTasksByUserId(Long id);

    void deleteByProcessInstanceId(String processInstanceId);
}
