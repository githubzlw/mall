/**
 * Copyright 2019 bejson.com
 */
package com.macro.mall.domain;

import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2019-02-28 10:40:44
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Variants {

    private long id;
    private long product_id;
    private String title;
    private String price;
    private String sku;
    private int position;
    private String inventory_policy;
    private String compare_at_price;
    private String fulfillment_service;
    private String inventory_management;
    private String option1;
    private String option2;
    private String option3;
    private String created_at;
    private String updated_at;
    private boolean taxable;
    private String barcode;
    private int grams;
    private String image_id;
    private String weight;
    private String weight_unit;
    private long inventory_item_id;
    private int inventory_quantity;
    private int old_inventory_quantity;
    private boolean requires_shipping;
    private String admin_graphql_api_id;
    private String country_code_of_origin;
    private List<PresentmentPrices> presentment_prices;

}