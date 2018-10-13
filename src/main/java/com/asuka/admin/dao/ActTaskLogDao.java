package com.asuka.admin.dao;

import com.asuka.admin.entity.act.ActTaskLog;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: hex1n
 * @Date: 2018/10/13 10:12
 */
@Mapper
public interface ActTaskLogDao{

    void saveActTaskLog(ActTaskLog actTaskLog);

    ActTaskLog getActTackLogById(int id);

    void updateActTaskLog(ActTaskLog actTaskLog);

    void deleteActTaskLog(ActTaskLog actTaskLog);

    List<ActTaskLog> selectAll();

}
