package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-09-09
 */
@Data
@ApiModel("运单的订单详情信息")
public class FulfillmentOrderItem {

    private Long orderNo;
    private Long itemId;
    private String title;
    private Integer quantity;
    private String sku;
    private String variantTitle;
    private String mainImg;

    private Long ourOrderId;

    private String shipTo;
    private String shippingDate;
    private String estimatedArrivalDate;
    private Integer shipFrom;


}
