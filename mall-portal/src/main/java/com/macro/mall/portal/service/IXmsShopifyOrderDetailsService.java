package com.macro.mall.portal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.XmsShopifyOrderDetails;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-12
 */
public interface IXmsShopifyOrderDetailsService extends IService<XmsShopifyOrderDetails> {

    int deleteByOrderNo(Long orderNo);

}
