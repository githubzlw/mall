package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 生成订单时传入的参数
 * Created by jack.luo on 2021/4/15.
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class XmsChromeUploadParam {

    @ApiModelProperty(value = "会员登录名（邮箱）")
    private String username;

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

    @ApiModelProperty(value = "抓取的运费")
    private String shippingFee;

    @ApiModelProperty(value = "抓取的运输方式")
    private String shippingBy;

}