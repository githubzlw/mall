package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;

public class TrafficFreightPort implements Serializable {
    private Integer id;

    @ApiModelProperty(value = "国家id")
    private Integer countryId;

    @ApiModelProperty(value = "运输方式")
    private String modeOfTransport;

    private String deliveryTime;

    private BigDecimal freightOver1000Price;

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

    public BigDecimal getFreightOver1000Price() {
        return freightOver1000Price;
    }

    public void setFreightOver1000Price(BigDecimal freightOver1000Price) {
        this.freightOver1000Price = freightOver1000Price;
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
        sb.append(", freightOver1000Price=").append(freightOver1000Price);
        sb.append(", del=").append(del);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}