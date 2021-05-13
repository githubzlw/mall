package com.macro.mall.portal.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.config
 * @date:2021-05-11
 */
@Data
@Component
public class ShopifyConfig {

    public static final String SHOPIFY_COM = ".myshopify.com";
    public static final String HMAC_ALGORITHM = "HmacSHA256";
    public static final String SHOPIFY_KEY = "sourcing:shopify:";

    @Value("${shopify.callback.url}")
    private String callBackUrl;
}
