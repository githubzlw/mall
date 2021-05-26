package com.macro.mall.shopify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsShopifyOrderAddress;
import com.macro.mall.mapper.XmsShopifyOrderAddressMapper;
import com.macro.mall.shopify.service.IXmsShopifyOrderAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * shopify订单地址 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-12
 */
@Service
public class XmsShopifyOrderAddressServiceImpl extends ServiceImpl<XmsShopifyOrderAddressMapper, XmsShopifyOrderAddress> implements IXmsShopifyOrderAddressService {

    @Autowired
    private XmsShopifyOrderAddressMapper xmsShopifyOrderAddressMapper;

    @Override
    public int deleteByOrderNo(Long orderNo) {

        QueryWrapper<XmsShopifyOrderAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsShopifyOrderAddress::getOrderNo, orderNo);
        return this.xmsShopifyOrderAddressMapper.delete(queryWrapper);
    }
}
