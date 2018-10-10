package com.asuka.admin.service;

import com.asuka.admin.entity.act.UserLeave;

import java.util.List;

/**
 * @Author: hex1n
 * @Date: 2018/10/6 9:28
 */
public interface UserLeaveService {

    List<UserLeave> selectListByPage(UserLeave userLeave);

    void insertUserLeave(UserLeave userLeave);

    void updateByPrimaryKeySelective(UserLeave userLeave);

}
