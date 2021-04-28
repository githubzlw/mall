package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-04-28
 */
@Data
public class EstimatedCostParam {

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

    private Double weight;

    private Double volume;


}
