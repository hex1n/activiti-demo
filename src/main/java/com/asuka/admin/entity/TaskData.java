package com.asuka.admin.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 流程描述信息实体
 *
 * @Author: hex1n
 * @Date: 2018/9/25 11:42
 */
@Data
public class TaskData implements Serializable {

    private static final long serialVersionUID = -5171511412013944174L;

    private String pKey;
    private String pType;
    private String pTaskName;
    private String pValue;
    private String pDesc;

}
