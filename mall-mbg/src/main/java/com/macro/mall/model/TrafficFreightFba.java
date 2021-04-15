package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;

public class TrafficFreightFba implements Serializable {
    private Integer id;

    @ApiModelProperty(value = "国家id")
    private Integer countryId;

    @ApiModelProperty(value = "运输方式")
    private String modeOfTransport;

    private String deliveryTime;

    @ApiModelProperty(value = "初始价，首重价")
    private BigDecimal firstHeavyPrice;

    @ApiModelProperty(value = "初始重量")
    private Double firstHeavy;

    private BigDecimal continuedHeavyPrice;

    @ApiModelProperty(value = "大于等于21kg运费")
    private BigDecimal freightOver21Price;

    private BigDecimal freightOver51Price;

    private BigDecimal freightOver101Price;

    private BigDecimal freightOver300Price;

    private BigDecimal freightOver501Price;

    private BigDecimal freightOver1001Price;

    @ApiModelProperty(value = "是否删除：1删除")
    private Integer del;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
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

    public BigDecimal getFirstHeavyPrice() {
        return firstHeavyPrice;
    }

    public void setFirstHeavyPrice(BigDecimal firstHeavyPrice) {
        this.firstHeavyPrice = firstHeavyPrice;
    }

    public Double getFirstHeavy() {
        return firstHeavy;
    }

    public void setFirstHeavy(Double firstHeavy) {
        this.firstHeavy = firstHeavy;
    }

    public BigDecimal getContinuedHeavyPrice() {
        return continuedHeavyPrice;
    }

    public void setContinuedHeavyPrice(BigDecimal continuedHeavyPrice) {
        this.continuedHeavyPrice = continuedHeavyPrice;
    }

    public BigDecimal getFreightOver21Price() {
        return freightOver21Price;
    }

    public void setFreightOver21Price(BigDecimal freightOver21Price) {
        this.freightOver21Price = freightOver21Price;
    }

    public BigDecimal getFreightOver51Price() {
        return freightOver51Price;
    }

    public void setFreightOver51Price(BigDecimal freightOver51Price) {
        this.freightOver51Price = freightOver51Price;
    }

    public BigDecimal getFreightOver101Price() {
        return freightOver101Price;
    }

    public void setFreightOver101Price(BigDecimal freightOver101Price) {
        this.freightOver101Price = freightOver101Price;
    }

    public BigDecimal getFreightOver300Price() {
        return freightOver300Price;
    }

    public void setFreightOver300Price(BigDecimal freightOver300Price) {
        this.freightOver300Price = freightOver300Price;
    }

    public BigDecimal getFreightOver501Price() {
        return freightOver501Price;
    }

    public void setFreightOver501Price(BigDecimal freightOver501Price) {
        this.freightOver501Price = freightOver501Price;
    }

    public BigDecimal getFreightOver1001Price() {
        return freightOver1001Price;
    }

    public void setFreightOver1001Price(BigDecimal freightOver1001Price) {
        this.freightOver1001Price = freightOver1001Price;
    }

    public Integer getDel() {
        return del;
    }

    public void setDel(Integer del) {
        this.del = del;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", countryId=").append(countryId);
        sb.append(", modeOfTransport=").append(modeOfTransport);
        sb.append(", deliveryTime=").append(deliveryTime);
        sb.append(", firstHeavyPrice=").append(firstHeavyPrice);
        sb.append(", firstHeavy=").append(firstHeavy);
        sb.append(", continuedHeavyPrice=").append(continuedHeavyPrice);
        sb.append(", freightOver21Price=").append(freightOver21Price);
        sb.append(", freightOver51Price=").append(freightOver51Price);
        sb.append(", freightOver101Price=").append(freightOver101Price);
        sb.append(", freightOver300Price=").append(freightOver300Price);
        sb.append(", freightOver501Price=").append(freightOver501Price);
        sb.append(", freightOver1001Price=").append(freightOver1001Price);
        sb.append(", del=").append(del);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}