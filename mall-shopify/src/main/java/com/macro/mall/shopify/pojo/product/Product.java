/**
 * Copyright 2019 bejson.com
 */
package com.macro.mall.shopify.pojo.product;

import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2019-02-28 10:40:44
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Product {

    private long id;
    private String title;
    private String body_html;
    private String vendor;
    private String product_type;
    private String weight_value;
    private String created_at;
    private String handle;
    private String updated_at;
    private String published_at;
    private String template_suffix;
    private String tags;
    private String published_scope;
    private String admin_graphql_api_id;
    private boolean published = false;
    private List<Variants> variants;
    private List<Options> options;
    private List<Images> images;

}