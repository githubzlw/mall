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
        XmsMsgParam xmsMsgParam =new XmsMsgParam();
        xmsMsgParam.setEmail("test@163.com");
        System.out.println(service.unreadMsgList(xmsMsgParam ,1,5));
    }

    @Test
    void readMsgList() {

        XmsMsgParam xmsMsgParam =new XmsMsgParam();
        System.out.println(service.readMsgList(xmsMsgParam ,1,5));
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