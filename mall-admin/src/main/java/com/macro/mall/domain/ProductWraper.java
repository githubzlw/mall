/**
 * Copyright 2019 bejson.com
 */
package com.macro.mall.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Auto-generated: 2019-02-28 13:16:11
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class ProductWraper {

    @ApiModelProperty(value ="商品信息",required = true)
    private Product product;
    @ApiModelProperty(value ="是否发布过",required = true)
    private boolean push=false;


}