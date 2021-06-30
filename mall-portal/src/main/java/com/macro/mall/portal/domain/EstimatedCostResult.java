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

    @ApiModelProperty("国家ID")
    private Integer countryId;

    @ApiModelProperty("原始运费")
    private String originalShippingFee;

    @ApiModelProperty("原始价格")
    private String originalProductPrice;

    @ApiModelProperty("原始重量")
    private Double originalWeight;

    @ApiModelProperty("原始体积")
    private Double originalVolume;

    @ApiModelProperty("预估价格")
    private String estimatedPrice;

    private XmsFbaFreightUnit freightUnit;

    private EstimatedCost busySellStandard;
    private EstimatedCost busySellPremium;

    private XmsTailFreightResult tailFreight;
}
