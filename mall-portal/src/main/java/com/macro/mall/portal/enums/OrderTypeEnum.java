package com.macro.mall.portal.enums;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.enums
 * @date:2021-08-24
 */
public enum OrderTypeEnum {
    SOURCING_ORDER("SC","sourcing的库存订单"),
    DELIVER_GOODS("DG","shopify发货的订单");


    OrderTypeEnum(String code, String note) {
        this.code = code;
        this.note = note;
    }

    private String code;
    private String note;

    public String getCode() {
        return code;
    }

    public String getNote() {
        return note;
    }
}
