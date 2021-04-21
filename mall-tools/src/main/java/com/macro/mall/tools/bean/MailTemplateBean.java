package com.macro.mall.tools.bean;

/**
 * @Author jack.luo
 * @create 2020/4/15 13:39
 * Description
 */

import com.macro.mall.common.enums.MailTemplateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public abstract class MailTemplateBean {

        /**
         * to
         */
        @NotEmpty
        private String to;

        /**
         * bcc
         */
        private String bcc;

        /**
         * subject
         */
        @NotEmpty
        private String subject;

        /**
         * body
         */
        private String body;

        /**
         * 邮件类型
         */
        @NotEmpty
        private MailTemplateType templateType;

        /**
         * 1:线上请求    2:线下请求
         */
        private int type = 1;

        /**
         * true:测试模板（不实际发送邮件）
         */
        private boolean isTest = true;


}
