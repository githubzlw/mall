package com.macro.mall.portal.enums;

import lombok.Getter;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.enums
 * @date:2021-09-08
 */
public enum PayTypeEnum {

    /**
     * 支付方式： 0是paypal支付，1 余额支付
     */

    PAYPAL(0, "paypal支付"),
    BALANCE(1, "余额支付");

    PayTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Getter
    private int code;
    @Getter
    private String desc;
}
