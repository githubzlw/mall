package com.macro.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.domain.XmsShopifyOrderinfoParam;
import com.macro.mall.entity.XmsShopifyOrderComb;
import com.macro.mall.entity.XmsShopifyOrderinfo;

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

}
