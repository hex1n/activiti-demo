package com.asuka.admin.config;

import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.stereotype.Component;

/**
 * 解决流程图乱码问题
 *
 * @Author: hex1n
 * @Date: 2018/10/4 23:21
 */
@Component
public class EncodeProcessEngineConfiguration implements ProcessEngineConfigurationConfigurer {

    @Override
    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
        processEngineConfiguration.setActivityFontName("宋体");
        processEngineConfiguration.setLabelFontName("宋体");
        processEngineConfiguration.setAnnotationFontName("宋体");
        System.out.println("EncodeProcessEngineConfiguration#############");
        System.out.println(processEngineConfiguration.getActivityFontName());
    }
}
