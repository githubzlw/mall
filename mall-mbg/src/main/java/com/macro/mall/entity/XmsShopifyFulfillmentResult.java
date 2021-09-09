package com.macro.mall.entity;

import lombok.Data;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.entity
 * @date:2021-09-09
 */
@Data
public class XmsShopifyFulfillmentResult {

    private XmsShopifyFulfillment fulfillment;

    private List<XmsShopifyFulfillmentItem> itemList;
}
