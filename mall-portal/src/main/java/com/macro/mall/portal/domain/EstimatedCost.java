package com.macro.mall.portal.domain;

import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: 预估运费bean
 * @date:2021-04-22
 */
@Data
public class EstimatedCost {


    private String estimatedPrice;
    private int shippingByFlag;// 0ImportX Standard  1 importXPremium

    private String cost;

    /**
     * ImportX Standard  15天
     * importXPremium  8-10天
     */
    private String deliveryTime = "15";// 交期

    private double weight;
}
