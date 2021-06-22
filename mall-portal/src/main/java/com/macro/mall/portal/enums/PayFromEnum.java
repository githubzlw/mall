package com.macro.mall.portal.enums;

import lombok.Getter;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.enums
 * @date:2021-06-22
 */
public enum PayFromEnum {
    NONE(0,"未知"),
    PURCHASE_INVENTORY(1,"采购库存"),
    SOURCING_ORDER(2,"sourcing下单"),
    RECHARGE(3,"充值");


    PayFromEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Getter
    private int code;
    @Getter
    private String desc;
}
