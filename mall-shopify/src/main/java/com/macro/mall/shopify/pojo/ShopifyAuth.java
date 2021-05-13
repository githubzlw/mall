package com.macro.mall.shopify.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ShopifyAuth implements Serializable {
    private Integer id;

    private String shopName;

    private String accessToken;

    private String scope;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public void setShopName(String shopName) {
        this.shopName = shopName == null ? null : shopName.trim();
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken == null ? null : accessToken.trim();
    }

    public void setScope(String scope) {
        this.scope = scope == null ? null : scope.trim();
    }

}