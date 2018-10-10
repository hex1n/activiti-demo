package com.asuka.admin.service.impl;

import com.asuka.admin.dao.UserLeaveDao;
import com.asuka.admin.entity.act.UserLeave;
import com.asuka.admin.service.UserLeaveService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: hex1n
 * @Date: 2018/10/6 9:34
 */
@Service
public class UserLeaveServiceImpl extends ServiceImpl<UserLeaveDao, UserLeave> implements UserLeaveService {


    @Resource
    private UserLeaveDao userLeaveDao;

    @Override
    public List<UserLeave> selectListByPage(UserLeave userLeave) {

        return userLeaveDao.selectListByPage(userLeave);
    }

    @Override
    public void insertUserLeave(UserLeave userLeave) {

        userLeaveDao.insert(userLeave);
    }

    @Override
    public void updateByPrimaryKeySelective(UserLeave userLeave) {

        baseMapper.updateById(userLeave);
    }
}
