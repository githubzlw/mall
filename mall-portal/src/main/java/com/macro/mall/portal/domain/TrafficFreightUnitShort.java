package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TrafficFreightUnitShort implements Serializable {

    private int id;
    @ApiModelProperty(value = "运输方式")
    private String modeOfTransport;

    @ApiModelProperty(value = "交期时间")
    private String deliveryTime;

    @ApiModelProperty(value = "国家id")
    private Integer countryId;

    @ApiModelProperty(value = "0:不需要拆包 1：需要拆包")
    private Integer split;

    @ApiModelProperty("总运费")
    private double totalFreight;

    @ApiModelProperty("折扣价格")
    private double discountedTotalPrice;

    @ApiModelProperty("我司成本运费")
    private double costAndFreightOfOurCompany;

    private static final long serialVersionUID = 3385965231L;


}