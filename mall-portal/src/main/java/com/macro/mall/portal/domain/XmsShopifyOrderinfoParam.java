package com.macro.mall.portal.domain;

import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-05-13
 */
@Data
public class XmsShopifyOrderinfoParam {

    private Long orderNo;

    private String shopifyName;

    private static final long serialVersionUID = 51678551L;

    private Integer pageNum;

    private Integer pageSize;
}
