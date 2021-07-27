package com.macro.mall.shopify.pojo;

import lombok.Getter;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.shopify.pojo
 * @date:2021-05-19
 */
@Getter
public enum FulfillmentStatusEnum {


    SHIPPED(1,"已发货"),
    PARTIAL(2,"部分发货"),
    UNSHIPPED(3,"未发货"),
    UNFULFILLED(4,"null 或partial"),
    RESTOCKED(5,"缺货");




    private int code;
    private String desc;


    FulfillmentStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}
