package com.macro.mall.portal.domain;

import com.macro.mall.entity.XmsCustomerSkuStock;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ShopifyPreOrderItem对象", description = "shopify的客户商品预采购商品")
public class ShopifyPreOrderItem {

    @ApiModelProperty(value = "订单号")
    private Long orderNo;

    private Long productId;
    private Long lineItemId;

    @ApiModelProperty(value = "sourcing后我司的商品图片")
    private String img;
    @ApiModelProperty(value = "sourcing后我司的商品价格")
    private String price;
    @ApiModelProperty(value = "免邮状态：0->非免邮；1->免邮")
    private Integer freeStatus;

    private Double weight;
    private Double volume;

    @ApiModelProperty(value = "需要采购的数量")
    private int needNumber;
    @ApiModelProperty(value = "已经购买的库存")
    private List<XmsCustomerSkuStock> stockList;

    @ApiModelProperty(value = "反馈")
    private String feedBack;

}
