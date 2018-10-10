package com.asuka.admin.dao;

import com.asuka.admin.entity.act.UserLeave;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * @Author: hex1n
 * @Date: 2018/10/6 9:35
 */
public interface UserLeaveDao extends BaseMapper<UserLeave> {

    List<UserLeave> selectListByPage(UserLeave userLeave);
}
