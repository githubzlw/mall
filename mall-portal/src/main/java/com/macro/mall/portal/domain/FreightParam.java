package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: 运费计算bean
 * @date:2021-04-14
 */
@Data
@ApiModel(value = "计算运费的参数Bean")
public class FreightParam {

    @ApiModelProperty("国家ID")
    private Integer countryId;

    @ApiModelProperty("商品总价格")
    private Double productCost;

    @ApiModelProperty("总重量")
    private Double totalWeight;

    @ApiModelProperty("b2c标识")
    private Integer b2cFlag;


}


