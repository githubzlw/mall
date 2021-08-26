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
public class ShopifyFreightResult extends ShopifyFreightParam{

    private Double standardFreight;

    private Double premiumFreight;

    private List<TrafficFreightUnitShort> airFreightList;




}


