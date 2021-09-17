package com.macro.mall.portal.domain;

import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-09-09
 */
@Data
public class ShopifyOrderDetailsShort {

    private Integer id;
    private Long orderNo;

    private Long variantId;
    private String title;
    private Integer quantity;
    private String sku;
    private String variantTitle;


    private Long productId;
    private Long lineItemId;
    private String mainImg;

}
