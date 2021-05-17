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
public class Options {

    private long id;
    private long product_id;
    private String name;
    private int position;
    private List<String> values;

}