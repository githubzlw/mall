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
@ApiModel(value = "计算FBA运费的参数Bean")
public class FbaFreightParam {

    @ApiModelProperty("国家ID")
    private Integer countryId;

    @ApiModelProperty(value = "运输模式: 1 进FBA, 2 进客户门点, 3 CIF")
    private Integer modeOfTransport;

    @ApiModelProperty(value = "邮编")
    private String zipCode;

    @ApiModelProperty(value = "CIF的港口")
    private String portName;

    @ApiModelProperty("总重量")
    private Double weight;

     @ApiModelProperty("总体积")
    private Double volume;


}


