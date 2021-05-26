package com.macro.mall.shopify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsShopifyOrderDetails;
import com.macro.mall.mapper.XmsShopifyOrderDetailsMapper;
import com.macro.mall.shopify.service.IXmsShopifyOrderDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-12
 */
@Service
public class XmsShopifyOrderDetailsServiceImpl extends ServiceImpl<XmsShopifyOrderDetailsMapper, XmsShopifyOrderDetails> implements IXmsShopifyOrderDetailsService {

    @Autowired
    private XmsShopifyOrderDetailsMapper xmsShopifyOrderDetailsMapper;

    @Override
    public int deleteByOrderNo(Long orderNo) {
        QueryWrapper<XmsShopifyOrderDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsShopifyOrderDetails::getOrderNo, orderNo);
        return xmsShopifyOrderDetailsMapper.delete(queryWrapper);
    }
}
