package com.macro.mall.portal.util;

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

    LiveProduct("LP", "YouLiveProduct的订单前缀"),
    SourcingList("SL", "SourcingList的订单前缀"),
    Balance("BL", "充值余额的订单前缀");

    OrderPrefixEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    private String name;
    private String desc;// 说明


}
