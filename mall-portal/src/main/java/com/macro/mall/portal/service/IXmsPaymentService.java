package com.macro.mall.portal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.entity.XmsPayment;
import com.macro.mall.portal.domain.XmsPaymentParam;

/**
 * <p>
 * 支付表 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-06
 */
public interface IXmsPaymentService extends IService<XmsPayment> {

    Page<XmsPayment> list(XmsPaymentParam paymentParam);
}
