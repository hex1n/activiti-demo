package com.asuka.admin.service;

import com.asuka.admin.entity.act.ActTaskLog;

import java.util.List;

/**
 * @Author: hex1n
 * @Date: 2018/10/13 10:14
 */
public interface ActTaskLogService {

    void saveActTaskLog(ActTaskLog actTaskLog);

    ActTaskLog getActTackLogById(int id);

    void updateActTaskLog(ActTaskLog actTaskLog);

    void deleteActTaskLog(ActTaskLog actTaskLog);

    List<ActTaskLog> selectAll();


}
