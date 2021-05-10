package com.macro.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsCustomerProduct;
import com.macro.mall.entity.XmsCustomerSkuStock;
import com.macro.mall.mapper.XmsCustomerSkuStockMapper;
import com.macro.mall.portal.domain.XmsCustomerSkuStockParam;
import com.macro.mall.portal.service.IXmsCustomerSkuStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<XmsCustomerSkuStock> queryByUserInfo(String userName, Long memberId) {
        LambdaQueryWrapper<XmsCustomerSkuStock> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(XmsCustomerSkuStock::getUsername, userName);
        return this.xmsCustomerSkuStockMapper.selectList(lambdaQuery);
    }

    @Override
    public Page<XmsCustomerSkuStock> list(XmsCustomerSkuStockParam skuStockParam) {

        Page<XmsCustomerSkuStock> page = new Page<>(skuStockParam.getPageNum(), skuStockParam.getPageSize());
        LambdaQueryWrapper<XmsCustomerSkuStock> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(XmsCustomerSkuStock::getUsername, skuStockParam.getUsername());
        lambdaQuery.orderByDesc(XmsCustomerSkuStock::getCreateTime);
        return this.xmsCustomerSkuStockMapper.selectPage(page, lambdaQuery);
    }

    @Override
    public int updateStateByOrderNo(String orderNo, Integer status) {
        LambdaUpdateWrapper<XmsCustomerSkuStock> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(XmsCustomerSkuStock::getOrderNo, orderNo).set(XmsCustomerSkuStock::getStatus, status);
        return this.xmsCustomerSkuStockMapper.update(null, lambdaUpdateWrapper);
    }
}
