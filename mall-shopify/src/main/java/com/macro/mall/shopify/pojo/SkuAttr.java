package com.macro.mall.shopify.pojo;

import lombok.Data;

@Data
public class SkuAttr {
	private String skuAttr;
	private String skuPropIds;
	private SkuVal skuVal;
    private String specId;
    private String skuId;
    private double fianlWeight;
    /*private List<String> wholesalePrice;*/
}
