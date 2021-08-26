package com.macro.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsListOfCountries对象", description="")
public class XmsListOfCountries implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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

    @ApiModelProperty(value = "shopify对应的国家名称")
    private String shopifyCountryName;


}
