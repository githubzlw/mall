package com.macro.mall.domain;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Description:铺货请求数据
 *
 * @author : zlw
 * @date : 2021-05-10
 */
@Data
public class ProductRequestWrap {
    @ApiModelProperty(value = "店铺名称",required = true)
    private String shopname;

    @ApiModelProperty(value = "商品id",required = true)
    private String pid;
    @ApiModelProperty(value = "网站",required = true)
    private int site;
    @ApiModelProperty(value = "商品选择的sku",required = true)
    private List<String> skus = Lists.newArrayList();
    @ApiModelProperty(value ="是否直接发布商品",required = true)
    private boolean published = false;
}