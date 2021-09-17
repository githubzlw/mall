package com.macro.mall.portal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsShopifyFulfillmentItem;
import com.macro.mall.mapper.XmsShopifyFulfillmentItemMapper;
import com.macro.mall.portal.dao.XmsShopifyFulfillmentItemDao;
import com.macro.mall.portal.domain.FulfillmentOrderItem;
import com.macro.mall.portal.domain.FulfillmentParam;
import com.macro.mall.portal.service.IXmsShopifyFulfillmentItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * shopify运单的item 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-09-09
 */
@Service
public class XmsShopifyFulfillmentItemServiceImpl extends ServiceImpl<XmsShopifyFulfillmentItemMapper, XmsShopifyFulfillmentItem> implements IXmsShopifyFulfillmentItemService {

    @Autowired
    private XmsShopifyFulfillmentItemDao xmsShopifyFulfillmentItemDao;

    @Override
    public List<FulfillmentOrderItem> queryShopifyOrderItems(FulfillmentParam fulfillmentParam) {
        return xmsShopifyFulfillmentItemDao.queryShopifyOrderItems(fulfillmentParam);
    }

    @Override
    public int queryShopifyOrderItemsCount(FulfillmentParam fulfillmentParam) {
        return xmsShopifyFulfillmentItemDao.queryShopifyOrderItemsCount(fulfillmentParam);
    }
}
