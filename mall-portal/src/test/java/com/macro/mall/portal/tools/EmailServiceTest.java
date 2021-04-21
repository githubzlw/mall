package com.macro.mall.portal.tools;

import com.macro.mall.common.enums.MailTemplateType;
import com.macro.mall.tools.bean.WelcomeMailTemplateBean;
import com.macro.mall.tools.service.EmailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by macro on 2018/8/27.
 * 前台商品查询逻辑单元测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailServiceTest {
    @Autowired
    private EmailService service;
    @Test
    public void send() throws Exception {

        WelcomeMailTemplateBean bean = new WelcomeMailTemplateBean();
        bean.setName("name1");
        bean.setTo("luohao518@163.com");
        bean.setTemplateType(MailTemplateType.WELCOME);
        bean.setSubject("welcome");
        service.send(bean);
    }
}
