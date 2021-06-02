package com.macro.mall.shopify.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShopifyConfig {

    @Value("${shopify.client_id}")
    public String SHOPIFY_CLIENT_ID;

    @Value("${shopify.client_secret}")
    public String SHOPIFY_CLIENT_SECRET;

    @Value("${shopify.api_key}")
    public String SHOPIFY_API_KEY;

    @Value("${shopify.api_key_secret}")
    public String SHOPIFY_API_KEY_SECRET;

    @Value("${shopify.api_key_shopname}")
    public String SHOPIFY_API_KEY_SHOPNAME;

    @Value("${shopify.scope}")
    public String SHOPIFY_SCOPE;

    @Value("${shopify.redirect_uri}")
    public String SHOPIFY_REDIRECT_URI;

    @Value("${shopify.uri_products}")
    public String SHOPIFY_URI_PRODUCTS;
    @Value("${shopify.uri_delete}")
    public String SHOPIFY_URI_DELETE;

    @Value("${shopify.uri_orders}")
    public String SHOPIFY_URI_ORDERS;

    @Value("${shopify.uri_oauth}")
    public String SHOPIFY_URI_OAUTH;

    @Value("${shopify.uri_custom_collections}")
    public String SHOPIFY_URI_CUSTOM_COLLECTIONS;

    @Value("${shopify.uri_smart_collections}")
    public String SHOPIFY_URI_SMART_COLLECTIONS;


    @Value("")
    public String SHOPUFY_HOST;


}