package com.macro.mall.tools.bean;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 邮件配置类，数据存覆盖式存入数据存
 */
@Data
public class EmailConfig {

    @NotBlank
    private String host;

    @NotBlank
    private String port;

    @NotBlank
    private String user;

    @NotBlank
    private String pass;

    @NotBlank
    private String fromUser;
}
