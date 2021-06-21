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

    @ApiModelProperty("原始运费")
    private String originalShippingFee;

    @ApiModelProperty("原始价格")
    private String originalProductPrice;

    @ApiModelProperty("国家ID-必填")
    private Integer countryId;

    @ApiModelProperty("重量-必填")
    private Double weight;

    @ApiModelProperty("体积-必填")
    private Double volume;


}
