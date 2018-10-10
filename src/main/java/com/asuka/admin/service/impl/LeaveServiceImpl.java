package com.asuka.admin.service.impl;

import com.asuka.admin.dao.LeaveDao;
import com.asuka.admin.entity.act.LeaveApply;
import com.asuka.admin.service.LeaveService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @Author: hex1n
 * @Date: 2018/10/7 11:23
 */
@Service
public class LeaveServiceImpl extends ServiceImpl<LeaveDao, LeaveApply> implements LeaveService {

    @Resource
    private LeaveDao leaveDao;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public ProcessInstance startProcess(LeaveApply leaveApply, String userId, Map<String, Object> variables) {

        ProcessInstance processInstance = null;
        try {
            leaveApply.setUserId(userId);
            String endTime = leaveApply.getEndTime();
            String beginTime = leaveApply.getBeginTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int days = 0;
            long begin = sdf.parse(beginTime).getTime();
            long end = sdf.parse(endTime).getTime();
            days = (int) ((end - begin) / (1000 * 60 * 60 * 24));
            leaveApply.setDays(days);


            leaveDao.save(leaveApply);
            //使用leaveApply表的主键作为businessKey,连接数据业务和流程数据
            String businessKey = String.valueOf(leaveApply.getId());

            identityService.setAuthenticatedUserId(userId);
            processInstance = runtimeService.startProcessInstanceByKey("leave", businessKey, variables);
            System.out.println(businessKey);
            String instanceId = processInstance.getId();
            leaveApply.setProcessInstanceId(instanceId);
            leaveDao.update(leaveApply);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return processInstance;
    }

    @Override
    public List<LeaveApply> selectListByPage(LeaveApply leaveApply) {


        return leaveDao.selectListByPage(leaveApply);
    }

    @Override
    public LeaveApply getLeaveByBusinessKey(int businessKey) {

        return leaveDao.get(businessKey);
    }

    @Override
    public LeaveApply findLeaveByProcessInstanceId(String processInstanceId) {


        return leaveDao.findLeaveByProcessInstanceId(processInstanceId);
    }
}
