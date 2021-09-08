package com.macro.mall.portal.enums;

import lombok.Getter;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.enums
 * @date:2021-09-08
 */
public enum PayStatusEnum {

    /**
     * 付款状态 0 失败(Failed) 1 成功(Success) 2进行中(Pending)
     */

    FAILED(0,"失败"),
    SUCCESS(1,"成功"),
    PENDING(2,"进行中");

    PayStatusEnum(int code, String desc){
        this.code =code;
        this.desc =desc;
    }

    @Getter
    private int code;
    @Getter
    private String desc;
}
