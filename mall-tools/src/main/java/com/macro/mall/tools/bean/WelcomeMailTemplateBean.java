package com.macro.mall.tools.bean;

import lombok.Data;

/**
 * @Author jack.luo
 * @create 2020/4/15 11:22
 * Description
 */
@Data
public class WelcomeMailTemplateBean extends MailTemplateBean {


    private String name;
    private String pass;
    private String from;
    private String activationCode;
}
