package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-05-13
 */
@Data
public class XmsShopifyOrderinfoParam {

    private static final long serialVersionUID = 51678551L;


    @ApiModelProperty(value = "店铺名称")
    private String shopifyName;

    @ApiModelProperty(value = "国家名称")
    private String countryName;

    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "product title或者url")
    private String url;

    private Integer pageNum;

    private Integer pageSize;
}
