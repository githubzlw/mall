package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;

public class TrafficFreightUnit implements Serializable {
    private Integer id;

    @ApiModelProperty(value = "运输方式")
    private String modeOfTransport;

    @ApiModelProperty(value = "交期时间")
    private String deliveryTime;

    @ApiModelProperty(value = "国家id")
    private Integer countryId;

    @ApiModelProperty(value = "默认重量g")
    private Double firstHeavy;

    @ApiModelProperty(value = "首重价")
    private BigDecimal firstHeavyPrice;

    @ApiModelProperty(value = "续重价格")
    private BigDecimal continuedHeavyPrice;

    @ApiModelProperty(value = "21kg以上/kg 运费")
    private BigDecimal bigHeavyPrice;

    @ApiModelProperty(value = "特殊商品默认重量g")
    private Double defaultWeightOfSpecial;

    @ApiModelProperty(value = "特殊商品基础运费（带电，粉末）-首重")
    private BigDecimal firstHeavyPriceOfSpecial;

    @ApiModelProperty(value = "特殊商品基础运费（带电，粉末）-续重")
    private BigDecimal continuedHeavyPriceOfSpecial;

    @ApiModelProperty(value = "特殊商品运费（带电或者粉末）-大商品")
    private BigDecimal bigHeavyPriceOfSpecial;

    @ApiModelProperty(value = "0:正常状态，1：删除状态")
    private Integer del;

    @ApiModelProperty(value = "0:不需要拆包 1：需要拆包")
    private Integer split;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModeOfTransport() {
        return modeOfTransport;
    }

    public void setModeOfTransport(String modeOfTransport) {
        this.modeOfTransport = modeOfTransport;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Double getFirstHeavy() {
        return firstHeavy;
    }

    public void setFirstHeavy(Double firstHeavy) {
        this.firstHeavy = firstHeavy;
    }

    public BigDecimal getFirstHeavyPrice() {
        return firstHeavyPrice;
    }

    public void setFirstHeavyPrice(BigDecimal firstHeavyPrice) {
        this.firstHeavyPrice = firstHeavyPrice;
    }

    public BigDecimal getContinuedHeavyPrice() {
        return continuedHeavyPrice;
    }

    public void setContinuedHeavyPrice(BigDecimal continuedHeavyPrice) {
        this.continuedHeavyPrice = continuedHeavyPrice;
    }

    public BigDecimal getBigHeavyPrice() {
        return bigHeavyPrice;
    }

    public void setBigHeavyPrice(BigDecimal bigHeavyPrice) {
        this.bigHeavyPrice = bigHeavyPrice;
    }

    public Double getDefaultWeightOfSpecial() {
        return defaultWeightOfSpecial;
    }

    public void setDefaultWeightOfSpecial(Double defaultWeightOfSpecial) {
        this.defaultWeightOfSpecial = defaultWeightOfSpecial;
    }

    public BigDecimal getFirstHeavyPriceOfSpecial() {
        return firstHeavyPriceOfSpecial;
    }

    public void setFirstHeavyPriceOfSpecial(BigDecimal firstHeavyPriceOfSpecial) {
        this.firstHeavyPriceOfSpecial = firstHeavyPriceOfSpecial;
    }

    public BigDecimal getContinuedHeavyPriceOfSpecial() {
        return continuedHeavyPriceOfSpecial;
    }

    public void setContinuedHeavyPriceOfSpecial(BigDecimal continuedHeavyPriceOfSpecial) {
        this.continuedHeavyPriceOfSpecial = continuedHeavyPriceOfSpecial;
    }

    public BigDecimal getBigHeavyPriceOfSpecial() {
        return bigHeavyPriceOfSpecial;
    }

    public void setBigHeavyPriceOfSpecial(BigDecimal bigHeavyPriceOfSpecial) {
        this.bigHeavyPriceOfSpecial = bigHeavyPriceOfSpecial;
    }

    public Integer getDel() {
        return del;
    }

    public void setDel(Integer del) {
        this.del = del;
    }

    public Integer getSplit() {
        return split;
    }

    public void setSplit(Integer split) {
        this.split = split;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", modeOfTransport=").append(modeOfTransport);
        sb.append(", deliveryTime=").append(deliveryTime);
        sb.append(", countryId=").append(countryId);
        sb.append(", firstHeavy=").append(firstHeavy);
        sb.append(", firstHeavyPrice=").append(firstHeavyPrice);
        sb.append(", continuedHeavyPrice=").append(continuedHeavyPrice);
        sb.append(", bigHeavyPrice=").append(bigHeavyPrice);
        sb.append(", defaultWeightOfSpecial=").append(defaultWeightOfSpecial);
        sb.append(", firstHeavyPriceOfSpecial=").append(firstHeavyPriceOfSpecial);
        sb.append(", continuedHeavyPriceOfSpecial=").append(continuedHeavyPriceOfSpecial);
        sb.append(", bigHeavyPriceOfSpecial=").append(bigHeavyPriceOfSpecial);
        sb.append(", del=").append(del);
        sb.append(", split=").append(split);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}