package com.macro.mall.shopify.pojo;

import lombok.Data;

@Data
public class SkuVal {
	private String actSkuCalPrice;
	private String actSkuMultiCurrencyCalPrice;
	private String actSkuMultiCurrencyDisplayPrice;
	private int availQuantity;
	private int inventory;
	private boolean isActivity;
	private String skuCalPrice;
	private String skuMultiCurrencyCalPrice;
	private String skuMultiCurrencyDisplayPrice;
	private String freeSkuPrice;
	private String actSkuPrice;
	private String skuPrice;
	private String key;
	private String value;
}
