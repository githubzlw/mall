package com.macro.mall.portal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.XmsShopifyFulfillment;
import com.macro.mall.portal.domain.FulfillmentParam;

/**
 * <p>
 * shopify的运单信息 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-09-09
 */
public interface IXmsShopifyFulfillmentService extends IService<XmsShopifyFulfillment> {

    Page<XmsShopifyFulfillment> list(FulfillmentParam fulfillmentParam);

}
