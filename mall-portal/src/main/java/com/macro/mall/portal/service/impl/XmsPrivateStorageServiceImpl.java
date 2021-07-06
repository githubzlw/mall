package com.macro.mall.portal.service.impl;

import com.macro.mall.portal.dao.XmsPrivateStorageDao;
import com.macro.mall.portal.domain.XmsCustomerProductQuery;
import com.macro.mall.portal.domain.XmsCustomerProductStockParam;
import com.macro.mall.portal.service.XmsPrivateStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.service.impl
 * @date:2021-06-28
 */
@Service
public class XmsPrivateStorageServiceImpl implements XmsPrivateStorageService {

    @Autowired
    private XmsPrivateStorageDao xmsPrivateStorageDao;

    @Override
    public List<XmsCustomerProductQuery> queryProductByParam(XmsCustomerProductStockParam productStockParam) {
        return xmsPrivateStorageDao.queryProductByParam(productStockParam);
    }
}
