package com.macro.mall.portal.dao;

import com.macro.mall.portal.domain.FulfillmentOrderItem;
import com.macro.mall.portal.domain.FulfillmentParam;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.dao
 * @date:2021-09-09
 */
public interface XmsShopifyFulfillmentItemDao {

    List<FulfillmentOrderItem> queryShopifyOrderItems(FulfillmentParam fulfillmentParam);

    int queryShopifyOrderItemsCount(FulfillmentParam fulfillmentParam);
}
