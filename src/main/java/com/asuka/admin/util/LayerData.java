package com.asuka.admin.util;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangl on 2017/11/27.
 * todo:
 */
@Data
public class LayerData<T> implements Serializable {
    private Integer code = 0;
    private Integer count;
    private List<T> data;
    private String msg = "";

}
