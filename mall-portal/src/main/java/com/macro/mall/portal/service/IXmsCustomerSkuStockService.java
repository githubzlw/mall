package com.macro.mall.portal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.XmsCustomerSkuStock;
import com.macro.mall.portal.domain.XmsCustomerSkuStockParam;

import java.util.List;

/**
 * <p>
 * 客户的库存 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-28
 */
public interface IXmsCustomerSkuStockService extends IService<XmsCustomerSkuStock> {

    List<XmsCustomerSkuStock> queryByUserInfo(String userName, Long memberId);

    Page<XmsCustomerSkuStock> list(XmsCustomerSkuStockParam skuStockParam);

    int updateStateByOrderNo(String orderNo, Integer status);

}
