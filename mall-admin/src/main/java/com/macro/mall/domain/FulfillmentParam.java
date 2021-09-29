package com.macro.mall.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.shopify.pojo
 * @date:2021-05-19
 */
@Data
public class FulfillmentParam {

    @ApiModelProperty("必填-shopify店铺名称")
    private String shopifyName;

    @ApiModelProperty("必填-shopify订单号")
    private Long orderNo;

    @ApiModelProperty("必填-运单号")
    private String trackingNumber;

    @ApiModelProperty("必填-运输公司")
    private String trackingCompany;

    private Long memberId;
}
