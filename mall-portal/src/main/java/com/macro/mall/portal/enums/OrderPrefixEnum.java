package com.macro.mall.portal.enums;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.util
 * @date:2021-05-10
 */
@ApiModel("订单前缀")
@Getter
public enum OrderPrefixEnum {

    PURCHASE_STOCK_ORDER("PS", "购买库存订单"),
    SHOPIFY_DELIVER_ORDER("SD","shopify发货的订单"),
    Balance("BL", "充值余额的订单前缀");

    OrderPrefixEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;// 说明


}
