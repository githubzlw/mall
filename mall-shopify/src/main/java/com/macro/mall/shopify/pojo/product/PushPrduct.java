package com.macro.mall.shopify.pojo.product;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description:铺货结果
 *
 * @author : Administrator
 * @date : 2020-03-12
 */
@Data
public class PushPrduct {
    @ApiModelProperty(value ="商品信息",required = true)
    private Product product;
}
