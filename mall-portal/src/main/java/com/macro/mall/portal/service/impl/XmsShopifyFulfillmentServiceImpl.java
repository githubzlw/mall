package com.macro.mall.portal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsShopifyFulfillment;
import com.macro.mall.mapper.XmsShopifyFulfillmentMapper;
import com.macro.mall.portal.domain.FulfillmentParam;
import com.macro.mall.portal.service.IXmsShopifyFulfillmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * shopify的运单信息 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-09-09
 */
@Service
public class XmsShopifyFulfillmentServiceImpl extends ServiceImpl<XmsShopifyFulfillmentMapper, XmsShopifyFulfillment> implements IXmsShopifyFulfillmentService {

    @Autowired
    private XmsShopifyFulfillmentMapper xmsShopifyFulfillmentMapper;

    @Override
    public Page<XmsShopifyFulfillment> list(FulfillmentParam fulfillmentParam) {

        Page<XmsShopifyFulfillment> page = new Page<>(fulfillmentParam.getPageNum(), fulfillmentParam.getPageSize());
        LambdaQueryWrapper<XmsShopifyFulfillment> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(XmsShopifyFulfillment::getShopifyName, fulfillmentParam.getShopifyName());

        if (StrUtil.isNotBlank(fulfillmentParam.getTrackingNumber())) {
            lambdaQuery.eq(XmsShopifyFulfillment::getTrackingNumber, fulfillmentParam.getTrackingNumber());
        }
        // beginTime
        if (StrUtil.isNotEmpty(fulfillmentParam.getBeginTime())) {
            lambdaQuery.ge(XmsShopifyFulfillment::getUpdateTm, fulfillmentParam.getBeginTime().substring(0, 10) + " 00:00:00");
        }
        // endTime
        if (StrUtil.isNotEmpty(fulfillmentParam.getEndTime())) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateTime = LocalDate.parse(fulfillmentParam.getEndTime().substring(0, 10), dateTimeFormatter);
            LocalDate plusDays = dateTime.plusDays(1);
            lambdaQuery.lt(XmsShopifyFulfillment::getUpdateTm, plusDays.format(dateTimeFormatter) + " 00:00:00");
        }

        lambdaQuery.orderByDesc(XmsShopifyFulfillment::getUpdateTm);
        return this.xmsShopifyFulfillmentMapper.selectPage(page, lambdaQuery);
    }
}
