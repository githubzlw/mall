package com.macro.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsShopifyOrderinfo;
import com.macro.mall.mapper.XmsShopifyOrderinfoMapper;
import com.macro.mall.portal.domain.XmsShopifyOrderinfoParam;
import com.macro.mall.portal.service.IXmsShopifyOrderinfoService;
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
    public Page<XmsShopifyOrderinfo> list(XmsShopifyOrderinfoParam orderinfoParam) {
        Page<XmsShopifyOrderinfo> page = new Page<>(orderinfoParam.getPageNum(), orderinfoParam.getPageSize());
        QueryWrapper<XmsShopifyOrderinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsShopifyOrderinfo::getShopifyName, orderinfoParam.getShopifyName());
        return this.xmsShopifyOrderinfoMapper.selectPage(page, queryWrapper);
    }
}
