package com.asuka;

import com.alibaba.fastjson.JSONObject;
import com.asuka.admin.dao.MenuDao;
import com.asuka.admin.entity.Menu;
import com.asuka.admin.entity.act.ActTaskLog;
import com.asuka.admin.service.ActTaskLogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MysiteformeApplicationTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(MysiteformeApplicationTests.class);

    @Autowired
    private MenuDao menuDao;

    @Autowired

    @Test
    public void contextLoads() {
        List<Menu> list = menuDao.getMenus(null);
        LOGGER.info(JSONObject.toJSONString(list));
    }



}
