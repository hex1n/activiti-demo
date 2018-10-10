package com.asuka.admin.dao;

import com.asuka.admin.entity.act.LeaveApply;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: hex1n
 * @Date: 2018/10/7 11:24
 */
@Mapper
public interface LeaveDao extends BaseMapper<LeaveApply> {
    void save(LeaveApply leaveApply);

    LeaveApply get(int id);

    void update(LeaveApply apply);

    List<LeaveApply> selectListByPage(LeaveApply leaveApply);

    LeaveApply findLeaveByProcessInstanceId(String processInstanceId);
}
