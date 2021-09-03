package com.macro.mall.shopify.pojo;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Description:铺货请求数据
 *
 * @author : Administrator
 * @date : 2020-02-25
 */
@Data
public class ProductRequestWrap {
    @ApiModelProperty(value = "店铺名称", required = true)
    private String shopname;

    @ApiModelProperty(value = "商品id", required = true)
    private String pid;
    @ApiModelProperty(value = "网站", required = true)
    private int site;
    @ApiModelProperty(value = "商品选择的sku", required = true)
    private List<String> skus = Lists.newArrayList();
    @ApiModelProperty(value = "是否直接发布商品", required = true)
    private boolean published = false;
    @ApiModelProperty(value = "是否详情", required = true)
    private boolean bodyHtml = false;

    private String collectionId;
    private String productType;
    private String productTags;

}
