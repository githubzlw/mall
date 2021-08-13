package com.macro.mall.portal.dao;

import com.macro.mall.entity.XmsShopifyOrderinfo;
import com.macro.mall.portal.domain.XmsShopifyOrderinfoParam;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.dao
 * @date:2021-08-12
 */
public interface XmsShopifyOrderinfoDao {

    List<XmsShopifyOrderinfo> queryForList(XmsShopifyOrderinfoParam xmsShopifyOrderinfoParam);
}
