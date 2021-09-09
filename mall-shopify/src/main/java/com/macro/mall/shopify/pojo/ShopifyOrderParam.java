package com.macro.mall.shopify.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.shopify.pojo
 * @date:2021-09-09
 */
@Data
@ApiModel("shopifyName更新订单状态")
public class ShopifyOrderParam {

    @ApiModelProperty("shopify店铺名")
    private String shopifyName;
    @ApiModelProperty("shopify订单号")
    private Long orderNo;
    @ApiModelProperty("shopify订单状态")
    private String fulfillmentStatus;
    @ApiModelProperty("标记为准备履行的时间")
    private String newFulfillAt;
}
