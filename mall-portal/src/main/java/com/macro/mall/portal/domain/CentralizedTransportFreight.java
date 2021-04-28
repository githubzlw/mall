package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: 集中运输运费
 * @date:2021-04-22
 */
@Data
@ApiModel(value = "集中运输运费")
public class CentralizedTransportFreight {

    @ApiModelProperty("最小重量")
    private double minWeight;

    @ApiModelProperty("最大重量")
    private double maxWeight;

    @ApiModelProperty("操作费")
    private double handlingFee;

    @ApiModelProperty("每千克运费")
    private double freightPerKilogram;

    @ApiModelProperty("总重量")
    private double totalWeight;

    @ApiModelProperty("总运费")
    private double totalFreight;

    private String deliveryTime = "5-8";// 交期

}
