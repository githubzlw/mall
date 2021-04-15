package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ListOfCountries implements Serializable {
    private Integer id;

    @ApiModelProperty(value = "国家名称")
    private String englishNameOfCountry;

    @ApiModelProperty(value = "国家中文名字")
    private String chineseNameOfCountry;

    @ApiModelProperty(value = "国家缩写,发起paypal支付传入的国家")
    private String countriesInCode;

    @ApiModelProperty(value = "区域号")
    private Integer areaNum;

    @ApiModelProperty(value = "区域名称")
    private String areaName;

    @ApiModelProperty(value = "不是非洲  0：是非洲  1")
    private Integer africaFlag;

    @ApiModelProperty(value = "属于那个海运区域:目前12个,默认没有0")
    private Integer cifFlag;

    private Integer del;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEnglishNameOfCountry() {
        return englishNameOfCountry;
    }

    public void setEnglishNameOfCountry(String englishNameOfCountry) {
        this.englishNameOfCountry = englishNameOfCountry;
    }

    public String getChineseNameOfCountry() {
        return chineseNameOfCountry;
    }

    public void setChineseNameOfCountry(String chineseNameOfCountry) {
        this.chineseNameOfCountry = chineseNameOfCountry;
    }

    public String getCountriesInCode() {
        return countriesInCode;
    }

    public void setCountriesInCode(String countriesInCode) {
        this.countriesInCode = countriesInCode;
    }

    public Integer getAreaNum() {
        return areaNum;
    }

    public void setAreaNum(Integer areaNum) {
        this.areaNum = areaNum;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getAfricaFlag() {
        return africaFlag;
    }

    public void setAfricaFlag(Integer africaFlag) {
        this.africaFlag = africaFlag;
    }

    public Integer getCifFlag() {
        return cifFlag;
    }

    public void setCifFlag(Integer cifFlag) {
        this.cifFlag = cifFlag;
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
        sb.append(", englishNameOfCountry=").append(englishNameOfCountry);
        sb.append(", chineseNameOfCountry=").append(chineseNameOfCountry);
        sb.append(", countriesInCode=").append(countriesInCode);
        sb.append(", areaNum=").append(areaNum);
        sb.append(", areaName=").append(areaName);
        sb.append(", africaFlag=").append(africaFlag);
        sb.append(", cifFlag=").append(cifFlag);
        sb.append(", del=").append(del);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}