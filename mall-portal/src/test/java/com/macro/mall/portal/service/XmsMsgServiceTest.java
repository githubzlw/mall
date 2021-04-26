package com.macro.mall.portal.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class XmsMsgServiceTest {



    @Autowired
    private IXmsMsgrecycleService xmsMsgrecycleService;


    @Test
    void insetMsgRecycle() {

        xmsMsgrecycleService.insetMsgRecycle("test11144@163.com",6);

    }

    @Test
    void updateMsgRecycle(){
        xmsMsgrecycleService.updateMsgRecycle("test11144@163.com",6);
    }
}