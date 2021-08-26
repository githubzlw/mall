package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-08-23
 */
@Data
@ApiModel("采购shopify订单的接受参数")
public class PurchaseShopifyOrderParam extends OrderPayParam{

    @ApiModelProperty("shopifyOrder表的ID")
    private Long shopifyOrderId;

    @ApiModelProperty("产品的pid:sku编码:数量")
    private List<String> skuCodeAndNumList;

    @ApiModelProperty(value = "下单的时候选择的国家 0china 1usa")
    private Integer shippingFrom;
}
