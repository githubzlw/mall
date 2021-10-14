package com.macro.mall.shopify.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.XmsShopifyOrderinfo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-12
 */
public interface IXmsShopifyOrderinfoService extends IService<XmsShopifyOrderinfo> {

    List<XmsShopifyOrderinfo> queryListByShopifyName(String shopifyName);

    List<XmsShopifyOrderinfo> queryListByOrderNo(Long orderNo);

    int setTrackNo(Long orderNo, String trackNo);

}
