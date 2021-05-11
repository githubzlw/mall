package com.macro.mall.portal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.entity.XmsPayment;
import com.macro.mall.mapper.XmsPaymentMapper;
import com.macro.mall.portal.domain.XmsPaymentParam;
import com.macro.mall.portal.service.IXmsPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 支付表 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-06
 */
@Service
public class XmsPaymentServiceImpl extends ServiceImpl<XmsPaymentMapper, XmsPayment> implements IXmsPaymentService {

    @Autowired
    private XmsPaymentMapper xmsPaymentMapper;

    @Override
    public Page<XmsPayment> list(XmsPaymentParam paymentParam) {
        Page<XmsPayment> page = new Page<>(paymentParam.getPageNum(), paymentParam.getPageSize());
        LambdaQueryWrapper<XmsPayment> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(XmsPayment::getUsername, paymentParam.getUsername());
        lambdaQuery.eq(XmsPayment::getPayStatus, 1);// 取成功的数据
        if (StrUtil.isNotEmpty(paymentParam.getOrderNo())) {
            lambdaQuery.eq(XmsPayment::getOrderNo, paymentParam.getOrderNo());
        }
        if (null != paymentParam.getPayStatus()) {
            lambdaQuery.eq(XmsPayment::getPayStatus, paymentParam.getPayStatus());
        }
        if (null != paymentParam.getPayType()) {
            lambdaQuery.eq(XmsPayment::getPayType, paymentParam.getPayType());
        }
        lambdaQuery.orderByDesc(XmsPayment::getCreateTime);
        return this.xmsPaymentMapper.selectPage(page, lambdaQuery);
    }
}
