package com.asuka.admin.service.impl;

import com.asuka.admin.dao.ActTaskLogDao;
import com.asuka.admin.entity.act.ActTaskLog;
import com.asuka.admin.service.ActTaskLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: hex1n
 * @Date: 2018/10/13 10:18
 */

@Service
public class ActTaskLogServiceImpl implements ActTaskLogService {

    @Resource
    private ActTaskLogDao actTaskLogDao;

    @Override
    public void saveActTaskLog(ActTaskLog actTaskLog) {


        actTaskLogDao.saveActTaskLog(actTaskLog);
    }

    @Override
    public ActTaskLog getActTackLogById(int id) {
        return null;
    }

    @Override
    public void updateActTaskLog(ActTaskLog actTaskLog) {

    }

    @Override
    public void deleteActTaskLog(ActTaskLog actTaskLog) {

    }

    @Override
    public List<ActTaskLog> selectAll() {
        return null;
    }
}
