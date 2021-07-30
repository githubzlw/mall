package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsCustomerSkuStock;
import com.macro.mall.mapper.XmsCustomerSkuStockMapper;
import com.macro.mall.service.IXmsCustomerSkuStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 客户的库存 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-28
 */
@Service
public class XmsCustomerSkuStockServiceImpl extends ServiceImpl<XmsCustomerSkuStockMapper, XmsCustomerSkuStock> implements IXmsCustomerSkuStockService {


    @Autowired
    private XmsCustomerSkuStockMapper xmsCustomerSkuStockMapper;

    @Override
    public int updateStateByOrderNo(String orderNo, Integer status) {
        LambdaUpdateWrapper<XmsCustomerSkuStock> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(XmsCustomerSkuStock::getOrderNo, orderNo).set(XmsCustomerSkuStock::getStatus, status);
        return this.xmsCustomerSkuStockMapper.update(null, lambdaUpdateWrapper);
    }
}
