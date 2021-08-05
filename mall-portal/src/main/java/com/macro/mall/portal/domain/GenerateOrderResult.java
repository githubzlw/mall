package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-05-10
 */
@ApiModel("生成订单计算付款金额结果")
@Data
public class GenerateOrderResult {

    @ApiModelProperty("订单号")
    private String orderNo;
    @ApiModelProperty("商品金额")
    private double productCost = 0;
    @ApiModelProperty("总金额")
    private double totalAmount;
    @ApiModelProperty("PayPal支付")
    private double payAmount = 0;
    @ApiModelProperty("余额支付")
    private double balanceAmount = 0;
    @ApiModelProperty("总运费")
    private double totalFreight;

    @ApiModelProperty("全部余额支付标识 1全部, 0部分或者没有")
    private int balanceFlag;
}
