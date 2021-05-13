package com.macro.mall.shopify.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * shopify请求参数
 */
@Data
public class ShopifyRequestWrap {

    @ApiModelProperty(value = "店铺名称",required = true)
    private String shopname;

    @ApiModelProperty(value = "商品数据",required = true)
    private ShopifyData data;
}
