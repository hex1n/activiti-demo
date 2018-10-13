package com.asuka.admin;

import com.asuka.admin.entity.act.ActTaskLog;
import com.asuka.admin.service.ActTaskLogService;
import com.xiaoleilu.hutool.util.ZipUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by wangl on 2018/1/14.
 * todo:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestZipUtil {

    @Test
    public void testZip() {
        String srcPat = "D://mysite";
        String zipPath = "D://test.zip";
        ZipUtil.zip(srcPat, zipPath);
    }

    @Autowired
    private ActTaskLogService actTaskLogService;

    @Test
    public void test() {

        ActTaskLog actTaskLog = new ActTaskLog();
        actTaskLog.setAppAction("1");
        actTaskLog.setAppOpinion("同意");
        actTaskLog.setCreateTime(new Date());
        actTaskLog.setBusId("111");
        // actTaskLog.setDefId("1234");
        actTaskLog.setProcessInstanceId("9999");
        actTaskLog.setTaskName("提交申请");
        actTaskLog.setTransactor("何鑫");
        actTaskLog.setTransactorId("666");

        actTaskLogService.saveActTaskLog(actTaskLog);
    }


    @Test
    public void test12() {
        ActTaskLog actTaskLog = new ActTaskLog();
        actTaskLog.setTaskName("提交申请");
        actTaskLog.setBusId("111");
        actTaskLog.setCreateTime(new Date());
        actTaskLog.setTransactorId("222");
        actTaskLogService.saveActTaskLog(actTaskLog);

    }

    @Test
    public void test111() {

        ArrayList<Object> objects = new ArrayList<>();
        objects.add(1);
        objects.add(1);
        objects.add(2);
        objects.add(3);
        objects.add(3);

        for (Object object : objects) {
            System.out.println(object);
        }

        System.out.println("============");
        HashSet<Object> hashSet = new HashSet<>();
        hashSet.addAll(objects);
        hashSet.addAll(objects);
        hashSet.addAll(objects);
        for (Object o : hashSet) {
            System.out.println(o);
        }
    }
}
