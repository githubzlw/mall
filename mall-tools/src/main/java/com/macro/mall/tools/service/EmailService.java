package com.macro.mall.tools.service;


import com.macro.mall.tools.bean.EmailConfig;
import com.macro.mall.tools.bean.MailTemplateBean;
import com.macro.mall.tools.bean.vo.EmailVo;


public interface EmailService {

    /**
     * 邮件配置
     *
     * @return /
     * @throws Exception /
     */
    EmailConfig getConfig() throws Exception;

    /**
     * 发送邮件
     * @param emailVo 邮件发送的内容
     * @param emailConfig 邮件配置
     * @throws Exception /
     */
    void send(EmailVo emailVo, EmailConfig emailConfig);

    void send(MailTemplateBean mailTemplateBean) throws Exception;
}
