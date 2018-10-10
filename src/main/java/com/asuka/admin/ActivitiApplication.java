package com.asuka.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hexin
 */
@SpringBootApplication(exclude = {org.activiti.spring.boot.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@MapperScan("com.asuka.admin.dao")
public class ActivitiApplication {


    public static void main(String[] args) {

//		System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(ActivitiApplication.class, args);
    }
}
