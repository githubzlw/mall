package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.XmsMsgParam;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class XmsMsgrecycleServiceTest {

    @Autowired
    private IXmsMsgService service;


    @Test
    void unreadMsgList() {
        System.out.println(service.unreadMsgList("test@qq.com",0));
    }

    @Test
    void readMsgList() {

        System.out.println(service.readMsgList("test@163.com",0));
    }

    @Test
    void insetMsgList(){

        XmsMsgParam xmsMsgParam = new XmsMsgParam();
        xmsMsgParam.setEmail("test888@163.com");
        xmsMsgParam.setContent("sdfasdfasde");
        xmsMsgParam.setTitle("mb");
        xmsMsgParam.setType(1);
        service.insetMsgList(xmsMsgParam);

    }
}