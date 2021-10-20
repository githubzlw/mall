package com.macro.mall.portal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.entity.XmsShopifyOrderComb;
import com.macro.mall.entity.XmsShopifyOrderinfo;
import com.macro.mall.entity.XmsShopifyPidInfo;
import com.macro.mall.portal.domain.FulfillmentOrderItem;
import com.macro.mall.portal.domain.ShopifyOrderDetailsShort;
import com.macro.mall.portal.domain.XmsShopifyOrderinfoParam;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-12
 */
public interface IXmsShopifyOrderinfoService extends IService<XmsShopifyOrderinfo> {

   CommonPage<XmsShopifyOrderComb> list(XmsShopifyOrderinfoParam orderinfoParam);

   int queryCount(XmsShopifyOrderinfoParam xmsShopifyOrderinfoParam);

   List<XmsShopifyPidInfo> queryByShopifyLineItem(String shopifyName, List<Long> lineItems, Long memberId);

   void dealShopifyOrderDetailsMainImg(Map<Long, List<ShopifyOrderDetailsShort>> shortMap);

   void dealItemImg(List<FulfillmentOrderItem> itemList);

   int setTrackNo(Long orderNo, String trackNo);

}
