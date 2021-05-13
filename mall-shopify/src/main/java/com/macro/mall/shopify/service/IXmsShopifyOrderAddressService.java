package com.macro.mall.shopify.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.XmsShopifyOrderAddress;

/**
 * <p>
 * shopify订单地址 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-12
 */
public interface IXmsShopifyOrderAddressService extends IService<XmsShopifyOrderAddress> {

    int deleteByOrderNo(Long orderNo);
}
