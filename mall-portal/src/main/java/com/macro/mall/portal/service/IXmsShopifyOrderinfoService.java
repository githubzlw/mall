package com.macro.mall.portal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.XmsShopifyOrderinfo;
import com.macro.mall.portal.domain.XmsShopifyOrderinfoParam;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-12
 */
public interface IXmsShopifyOrderinfoService extends IService<XmsShopifyOrderinfo> {

    Page<XmsShopifyOrderinfo> list(XmsShopifyOrderinfoParam orderinfoParam);

}
