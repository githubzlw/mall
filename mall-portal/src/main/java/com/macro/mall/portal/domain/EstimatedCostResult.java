package com.macro.mall.portal.domain;

import com.macro.mall.entity.XmsFbaFreightUnit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: 预估运费bean
 * @date:2021-04-22
 */
@Data
public class EstimatedCostResult {


    @ApiModelProperty("原始价格")
    private Double originalPrice;

    @ApiModelProperty("运费")
    private Double originalShippingFee;

    @ApiModelProperty("原始价格")
    private Double originalWeight;

    @ApiModelProperty("原始价格")
    private Double productPrice;

    @ApiModelProperty("国家ID")
    private Integer countryId;


    private XmsFbaFreightUnit freightUnit;

    private EstimatedCost importXStandard;
    private EstimatedCost importXPremium;
}
