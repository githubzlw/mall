package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: 运费计算bean
 * @date:2021-04-14
 */
@Data
@ApiModel(value = "计算shopify运费的参数Bean")
public class ShopifyFreightParam {

    @ApiModelProperty(value = "运输模式: 0 CHINA, 1 USA")
    private Integer transportType;

    @ApiModelProperty("免邮总重量")
    private Double freeShippingWeight;

    @ApiModelProperty("免邮总体积")
    private Double freeShippingVolume;
    @ApiModelProperty("免邮商品总价格")
    private Double freeShippingProductCost;

    @ApiModelProperty("非免邮总重量")
    private Double nonFreeShippingWeight;

    @ApiModelProperty("非免邮总体积")
    private Double nonFreeShippingVolume;

     @ApiModelProperty("非免邮商品总价格")
    private Double nonFreeShippingProductCost;

    @ApiModelProperty("单个最大重量")
    private Double singleMaxWeight;

    @ApiModelProperty("单个最大体积")
    private Double singleMaxVolume;

    @ApiModelProperty("国家ID")
    private Integer countryId;



}


