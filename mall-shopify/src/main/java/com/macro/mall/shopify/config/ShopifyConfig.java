package com.macro.mall.shopify.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShopifyConfig {

    @Value("${shopify.client_id}")
    public String SHOPIFY_CLIENT_ID;

    @Value("${shopify.client_secret}")
    public String SHOPIFY_CLIENT_SECRET;

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

    @Value("${shopify.uri_put_orders}")
    public String SHOPIFY_URI_PUT_ORDERS;

    @Value("${shopify.uri_post_fulfillment_orders}")
    public String SHOPIFY_URI_POST_FULFILLMENT_ORDERS;

    @Value("${shopify.uri_post_fulfillment_service}")
    public String SHOPIFY_URI_POST_FULFILLMENT_SERVICE;

    @Value("${shopify.fulfillment_service_callback}")
    public String SHOPIFY_FULFILLMENT_SERVICE_CALLBACK;

    @Value("${shopify.uri_query_orders}")
    public String SHOPIFY_URI_QUERY_ORDERS;

    @Value("${shopify.uri_query_variants}")
    public String SHOPIFY_URI_QUERY_VARIANTS;

    @Value("${shopify.uri_query_inventory_levels}")
    public String SHOPIFY_URI_QUERY_INVENTORY_LEVELS;

    @Value("${shopify.uri_products_imgs}")
    public String SHOPIFY_URI_PRODUCTS_IMGS;

    @Value("${shopify.uri_countries_list}")
    public String SHOPIFY_URI_COUNTRIES_LIST;

    @Value("${shopify.uri_put_custom_collections}")
    public String SHOPIFY_URI_PUT_CUSTOM_COLLECTIONS;

    @Value("${shopify.uri_put_smart_collections}")
    public String SHOPIFY_URI_PUT_SMART_COLLECTIONS;

    @Value("${shopify.uri_put_products}")
    public String SHOPIFY_URI_PUT_PRODUCTS;

    @Value("${shopify.uri_delete_products}")
    public String SHOPIFY_URI_DELETE_PRODUCTS;


}