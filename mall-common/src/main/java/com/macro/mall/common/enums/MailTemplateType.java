package com.macro.mall.common.enums;

import lombok.Data;
import lombok.ToString;

/**
 * @author jack.luo
 * @date 2018/10/23
 */
@ToString
public enum MailTemplateType {


    WELCOME("WELCOME", "welcome.html");


    private String name;
    private String fileName;

    public String getFileName(){
        return this.fileName;
    }

    private MailTemplateType(String name, String fileName) {

        this.name = name;
        this.fileName = fileName;
    }

}
