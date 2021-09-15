package com.macro.mall.entity;

import lombok.Data;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-08-12
 */
@Data
public class XmsShopifyOrderComb extends XmsShopifyOrderinfo {

    private List<XmsShopifyOrderDetails> detailsList;
    private XmsShopifyOrderAddress addressInfo;

    private Long totalQuantity;
    private String userName;
}
