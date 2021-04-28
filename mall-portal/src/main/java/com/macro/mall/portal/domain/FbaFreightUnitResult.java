package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class FbaFreightUnitResult {

    @ApiModelProperty(value = "国家ID")
    private Integer countryId;

    private String countryEn;

    @ApiModelProperty(value = "运输模式: 1 进FBA, 2 进客户门点")
    private Integer modeOfTransport;

    @ApiModelProperty(value = "运输模式的运输方式 1 FBA方式, 2 传统海运")
    private Integer typeOfMode;

    @ApiModelProperty(value = "重量运费")
    private Double weightPrice;

    @ApiModelProperty(value = "体积运费")
    private Double volumePrice;

    @ApiModelProperty(value = "单证费")
    private Double documentFee;

    @ApiModelProperty(value = "操作费")
    private Double handlingFee;

    @ApiModelProperty(value = "清关费")
    private Double clearanceFee;

    @ApiModelProperty(value = "AMS")
    private Double amsFree;

    @ApiModelProperty(value = "ISF")
    private Double isfFree;

    @ApiModelProperty(value = "报关费")
    private Double customsCharge;

    @ApiModelProperty(value = "本地拖车费")
    private Double localTruckingFee;

    @ApiModelProperty(value = "海运费")
    private Double seaFreight;

    @ApiModelProperty(value = "尾端拖车费")
    private Double endInTowing;

    @ApiModelProperty(value = "仓储费")
    private Double storageCharges;

    private static final long serialVersionUID = 454545236L;

    private String deliveryTime = "35";// 交期

    private double weight;// 重量
    private double volume;// 体积
    private double rbmRate;// 人名币汇率

    private double totalPrice;// 总运费


    public String getModeOfTransportDesc() {
        //fba类型: 1 进FBA, 2 进客户门点, 3 到港口
        switch (this.modeOfTransport) {
            case 1:
                return "FBA";
            case 2:
                return "DR2DR";
            case 3:
                return "PORT";
            default:
                return null;
        }
    }

}
