package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: paypal支付传递参数
 * @date:2021-04-27
 */
@Data
public class PayPalParam {

    @ApiModelProperty(value = "总金额")
    private Double totalAmount;

    @ApiModelProperty(value = "订单号")
    private String orderNo;
    @ApiModelProperty(value = "消费信息")
    private String customMsg;

    @ApiModelProperty(value = "网站名称")
    private String siteName;

    @ApiModelProperty(value = "回调页面地址")
    private String successUrlType;
}
