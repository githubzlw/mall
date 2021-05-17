package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importExpress.common.pojo
 * @date:2021-04-01
 */
@Data
@ApiModel(value = "网站buyForMe", description = "buyForMe的Bean")
public class SiteSourcing {

    @ApiModelProperty("PID")
    private String pid;
    @ApiModelProperty("BFM的商品URL")
    private String url;
    @ApiModelProperty("BFM的商品名称")
    private String name;
    @ApiModelProperty("BFM的图片链接")
    private String img;

    @ApiModelProperty("sku相关数据")
    private String skuInfo;

    @ApiModelProperty("平均每日订单量")
    private int averageDailyOrder;
    @ApiModelProperty("仅一次订单量")
    private int oneTimeOrderOnly;

    private double price;

    private double weight;

    private String unit;

    /**
     * 1 速卖通 2 taobao 3...
     */
    private int siteFlag;

    @ApiModelProperty("BFM的类别ID")
    private String catid;

    @ApiModelProperty("备注或者补充信息")
    private String data;

    @ApiModelProperty("客户ID")
    private long userId;

    @ApiModelProperty("客户名称")
    private String userName;

    @ApiModelProperty("选择类型 1:Drop Shipping  2:Wholesale and Bulk Shipping 3 Transportation Only:  4:Product Customization")
    private Integer chooseType;

    @ApiModelProperty("运输方式  1进FBA, 2 进客户门点, 3 CIF")
    private Integer typeOfShipping;

    @ApiModelProperty("目的国家")
    private String countryName;

    @ApiModelProperty("目的州或者城市")
    private String stateName;

    @ApiModelProperty("定制类型 1:changePackaging 2:changeColor,Material 3:Improve Quality 4:changeShape")
    private Integer customType;

}
