package com.macro.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.XmsCustomerSkuStock;

/**
 * <p>
 * 客户的库存 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-28
 */
public interface IXmsCustomerSkuStockService extends IService<XmsCustomerSkuStock> {

    int updateStateByOrderNo(String orderNo, Integer status);

}
