package com.macro.mall.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.entity
 * @date:2021-09-01
 */
@ApiModel(value = "XmsShopifyOrderAddress对象", description = "shopify订单地址")
@Data
public class XmsShopifyOrderAddressParam {
    private Integer id;
    private String firstName;

    private String address1;

    private String phone;
    private String zip;
    private String city;
    private String province;

    private String country;

    private String lastName;

    private String address2;
    private Long orderNo;
}
