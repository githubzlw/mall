package com.macro.mall.portal.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * chrome插件传入的参数
 * Created by jack.luo on 2021/4/15.
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class XmsChromeUploadParam {

    @ApiModelProperty(value = "会员登录token")
    private String token;

    @ApiModelProperty(value = "抓取的网址")
    private String url;

    @ApiModelProperty(value = "抓取的标题")
    private String title;

    @ApiModelProperty(value = "抓取的价格")
    private String price;

    @ApiModelProperty(value = "抓取的moq")
    private String moq;

    @ApiModelProperty(value = "抓取的橱窗图")
    private String images;

    @ApiModelProperty(value = "抓取的折扣")
    private String off;

    @ApiModelProperty(value = "抓取的sku")
    private String sku;

    @ApiModelProperty(value = "抓取的type")
    private String type;

    @ApiModelProperty(value = "抓取的交期")
    private String leadTime;

    @ApiModelProperty(value = "抓取的产品详情")
    private String productDetail;

    @ApiModelProperty(value = "抓取的产品描述")
    private String productDescription;

    @ApiModelProperty(value = "抓取的运费")
    private String shippingFee;

    @ApiModelProperty(value = "抓取的运输方式")
    private String shippingBy;

}