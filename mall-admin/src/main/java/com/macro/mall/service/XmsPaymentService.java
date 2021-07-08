package com.macro.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.domain.XmsPaymentParam;
import com.macro.mall.entity.XmsPayment;
import com.macro.mall.model.UmsMember;

import java.util.List;

/**
 * 支付信息管理Service
 * Created by macro on 2018/4/26.
 */
public interface XmsPaymentService {

    /**
     * 支付信息
     */
    Page<XmsPayment> list(XmsPaymentParam paymentParam);


}
