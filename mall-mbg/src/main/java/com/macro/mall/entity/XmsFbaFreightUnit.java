package com.macro.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * fba的国家和运费
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsFbaFreightUnit对象", description="fba的国家和运费")
public class XmsFbaFreightUnit implements Serializable {
    private Integer id;

    @ApiModelProperty(value = "国家ID")
    private Integer countryId;

    private String countryEn;

    @ApiModelProperty(value = "运输模式: 1 进FBA, 2 进客户门点, 3CIF")
    private Integer modeOfTransport;

    @ApiModelProperty(value = "运输模式的运输方式 1 FBA方式, 2 传统海运")
    private Integer typeOfMode;

    @ApiModelProperty(value = "邮编")
    private String zipCode;

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

    private Date createTime;

    private static final long serialVersionUID = 994875236L;


    private String deliveryTime = "35";// 交期

    @ApiModelProperty(value = "CIF的港口")
    private String portName;

    private int productFlag;// 是否是产品单页查询
    private double weight;// 重量
    private double volume;// 体积
    private double rmbRate;// 人名币汇率
    private double packingFee;//打包费
    private double productCost;//总商品费用
    private double totalPrice;// 总运费

    public double calculateTotalPrice(double sourceVolume, double currencyRate) {
        return this.divide(documentFee + handlingFee + clearanceFee + amsFree + isfFree + customsCharge +
                (localTruckingFee + seaFreight + endInTowing + storageCharges) * sourceVolume, 1D);
    }

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

    private Double divide(Double value1, Double value2) {
        BigDecimal result = BigDecimal.valueOf(value1 / value2).setScale(2, BigDecimal.ROUND_HALF_UP);
        return result.doubleValue();
    }

}
