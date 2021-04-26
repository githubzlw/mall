package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.XmsMsgParam;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class XmsMsgServiceTest {

    @Autowired
    private IXmsMsgService service;

    @Test
    void unreadMsgList() {
        XmsMsgParam xmsMsgParam = new XmsMsgParam();
        xmsMsgParam.setType(0);
        System.out.println(service.unreadMsgList(xmsMsgParam));
    }

    @Test
    void readMsgList() {
    }
}