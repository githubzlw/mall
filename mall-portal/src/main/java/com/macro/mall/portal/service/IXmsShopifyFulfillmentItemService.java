package com.macro.mall.portal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.XmsShopifyFulfillmentItem;
import com.macro.mall.portal.domain.FulfillmentOrderItem;
import com.macro.mall.portal.domain.FulfillmentParam;

import java.util.List;

/**
 * <p>
 * shopify运单的item 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-09-09
 */
public interface IXmsShopifyFulfillmentItemService extends IService<XmsShopifyFulfillmentItem> {


    List<FulfillmentOrderItem> queryShopifyOrderItems(FulfillmentParam fulfillmentParam);

    int queryShopifyOrderItemsCount(FulfillmentParam fulfillmentParam);

}
