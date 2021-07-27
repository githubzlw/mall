package com.macro.mall.shopify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsShopifyOrderinfo;
import com.macro.mall.mapper.XmsShopifyOrderinfoMapper;
import com.macro.mall.shopify.service.IXmsShopifyOrderinfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-12
 */
@Service
@Slf4j
public class XmsShopifyOrderinfoServiceImpl extends ServiceImpl<XmsShopifyOrderinfoMapper, XmsShopifyOrderinfo> implements IXmsShopifyOrderinfoService {

    @Autowired
    private XmsShopifyOrderinfoMapper xmsShopifyOrderinfoMapper;

    @Override
    public List<XmsShopifyOrderinfo> queryListByShopifyName(String shopifyName) {
        QueryWrapper<XmsShopifyOrderinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsShopifyOrderinfo::getShopifyName, shopifyName);
        return this.xmsShopifyOrderinfoMapper.selectList(queryWrapper);
    }

    @Override
    public List<XmsShopifyOrderinfo> queryListByOrderNo(Long orderNo) {
        QueryWrapper<XmsShopifyOrderinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsShopifyOrderinfo::getOrderNo, orderNo);
        return this.xmsShopifyOrderinfoMapper.selectList(queryWrapper);
    }
}
