package com.asuka.admin.entity.act;

import com.google.common.collect.Lists;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: hex1n
 * @Date: 2018/10/7 11:04
 */
@Data
public class LeaveApply implements Serializable {

    private int id;
    private String processInstanceId;
    private String userId;
    private String beginTime;
    private String endTime;
    private String reason;
    private int days;
    private String taskName;

    /**
     * 请假单审核信息
     */
    private List<LeaveOpinion> opinionList = Lists.newArrayList();

    public void leaveOpAdd(LeaveOpinion leaveOpinion) {
        this.opinionList.add(leaveOpinion);
    }

    public void leaveOpAddAll(List<LeaveOpinion> leaveOpinionList) {
        this.opinionList.addAll(leaveOpinionList);
    }

    public List<LeaveOpinion> getOpinionList() {
        return opinionList;
    }

    public void setOpinionList(List<LeaveOpinion> opinionList) {
        this.opinionList = opinionList;
    }

}
