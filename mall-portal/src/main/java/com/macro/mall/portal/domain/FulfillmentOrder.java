package com.macro.mall.portal.domain;

import com.macro.mall.entity.XmsShopifyOrderAddress;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-09-09
 */
@Data
@ApiModel("运单的订单信息")
public class FulfillmentOrder {
    private Long orderId;
    private String ourOrderNo;
    private String shopifyName;
    private String shipmentStatus;

    private String trackingCompany;
    private String trackingNumber;
    private String trackingNumberUrl;
    private String createdAt;
    private String updatedAt;

    private XmsShopifyOrderAddress orderAddress;

    private List<ShopifyOrderDetailsShort> itemList;
}
