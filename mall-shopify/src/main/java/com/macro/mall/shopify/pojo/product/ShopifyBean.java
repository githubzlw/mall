package com.macro.mall.shopify.pojo.product;

import lombok.Data;

@Data
public class ShopifyBean {
    private Integer id;
    private String shopifyName;
    private String shopifyPid;
    private String pid;
    private String shopifyInfo;
    private String createTime;
    private int publish;
}
