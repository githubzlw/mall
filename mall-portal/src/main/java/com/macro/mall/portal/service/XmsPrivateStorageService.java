package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.XmsCustomerProductQuery;
import com.macro.mall.portal.domain.XmsCustomerProductStockParam;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.service
 * @date:2021-06-28
 */
public interface XmsPrivateStorageService {

    List<XmsCustomerProductQuery> queryProductByParam(XmsCustomerProductStockParam productStockParam);
}
