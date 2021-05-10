package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-05-06
 */
@Data
@ApiModel("支付订单的接受参数")
public class OrderPayParam {

    @ApiModelProperty("运输方式")
    private String modeOfTransportation;

    @ApiModelProperty("交期")
    private String deliveryTime;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty(value = "收货人姓名")
    private String receiverName;

    @ApiModelProperty(value = "收货人电话")
    private String receiverPhone;

}
