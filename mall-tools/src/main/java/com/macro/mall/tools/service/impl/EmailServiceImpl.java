package com.macro.mall.tools.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.setting.dialect.Props;
import com.macro.mall.common.enums.MailTemplateType;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.common.util.EncryptUtils;
import com.macro.mall.tools.bean.EmailConfig;
import com.macro.mall.tools.bean.MailTemplateBean;
import com.macro.mall.tools.bean.WelcomeMailTemplateBean;
import com.macro.mall.tools.bean.vo.EmailVo;
import com.macro.mall.tools.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.Collections;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {


    @Override
    public EmailConfig getConfig() throws Exception {
        EmailConfig emailConfig = new EmailConfig();
        Props props = new Props("mail.properties");
        emailConfig.setHost(props.getStr("host"));
        emailConfig.setFromUser(props.getStr("fromUser"));
        emailConfig.setPass(props.getStr("pass"));
        emailConfig.setPort(props.getStr("port"));
        emailConfig.setUser(props.getStr("user"));
        return emailConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void send(EmailVo emailVo, EmailConfig emailConfig) {
        if (emailConfig.getHost() == null) {
            Asserts.fail("请先配置，再操作");
        }
        // 封装
        MailAccount account = new MailAccount();
        // 设置用户
//        String user = emailConfig.getFromUser().split("@")[0];
        account.setUser(emailConfig.getUser());
        account.setHost(emailConfig.getHost());
        account.setPort(Integer.parseInt(emailConfig.getPort()));
        account.setAuth(true);

        // 发送
        try {
            // 对称解密
            //account.setPass(EncryptUtils.desDecrypt(emailConfig.getPass()));
            account.setPass(emailConfig.getPass());

            account.setFrom(emailConfig.getUser() + "<" + emailConfig.getFromUser() + ">");
            // ssl方式发送
            account.setSslEnable(true);
            // 使用STARTTLS安全连接
            account.setStarttlsEnable(true);
            String content = emailVo.getContent();

            int size = emailVo.getTos().size();
            Mail.create(account)
                    .setTos(emailVo.getTos().toArray(new String[size]))
                    .setTitle(emailVo.getSubject())
                    .setContent(content)
                    .setHtml(true)
                    //关闭session
                    .setUseGlobalSession(false)
                    .send();
        } catch (Exception e) {
            log.error("send mail", e);
            Asserts.fail(e.getMessage());
        }
    }

    /**
     * 发送邮件
     *
     * @param mailTemplateBean
     */
    @Override
    public void send(MailTemplateBean mailTemplateBean) throws Exception {

        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        MailTemplateType templateType = mailTemplateBean.getTemplateType();
        /*File file = ResourceUtils.getFile("classpath:" + "template/email/" + templateType.getFileName());
        Template template = engine.getTemplate(file.getCanonicalPath().replace("\\","/"));*/
        Template template = engine.getTemplate("template/email/" + templateType.getFileName());

        EmailConfig config = this.getConfig();
        EmailVo emailVo = null;

        Dict dict = Dict.create();
        WelcomeMailTemplateBean destBean;
        switch (templateType) {
            case WELCOME:
                //欢迎邮件
                destBean = (WelcomeMailTemplateBean) mailTemplateBean;

                dict.set("name", destBean.getName());
                dict.set("email", destBean.getName());
                dict.set("pass", destBean.getPass());
                emailVo = new EmailVo(Collections.singletonList(destBean.getTo()), destBean.getSubject(), template.render(dict));
                break;
            case ACCOUNT_UPDATE:
                //激活邮件
                destBean = (WelcomeMailTemplateBean) mailTemplateBean;
                dict.set("name", destBean.getName());
                dict.set("email", destBean.getName());
                dict.set("activeLink", destBean.getActivationCode());
                emailVo = new EmailVo(Collections.singletonList(destBean.getTo()), destBean.getSubject(), template.render(dict));
                break;
            default:
                Asserts.fail("该种类型未开发");
        }
        this.send(emailVo, config);

    }
}
