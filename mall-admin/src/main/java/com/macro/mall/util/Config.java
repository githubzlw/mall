package com.macro.mall.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${shopify.api_key}")
    public String SHOPIFY_API_KEY;

    @Value("${shopify.api_key_secret}")
    public String SHOPIFY_API_KEY_SECRET;

    @Value("${shopify.uri_products}")
    public String SHOPIFY_URI_PRODUCTS;


}