package com.asuka.admin.config;

import com.asuka.admin.freemark.*;
import freemarker.template.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by wangl on 2017/11/26.
 * todo:
 */
@Component
public class FreemarkerConfig {

    @Autowired
    private Configuration configuration;



    @Autowired
    private SysUserTempletModel sysUserTempletModel;


    @PostConstruct
    public void setSharedVariable() {
        //获取系统用户信息
        configuration.setSharedVariable("sysuser", sysUserTempletModel);
    }
}
