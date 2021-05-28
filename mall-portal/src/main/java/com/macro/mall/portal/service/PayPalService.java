package com.macro.mall.portal.service;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.enums.PayPalPaymentIntentEnum;
import com.macro.mall.portal.enums.PayPalPaymentMethodEnum;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import java.io.IOException;

/**
 * @author jack.luo
 * @date 2019/12/6
 */
public interface PayPalService {
    /**
     * 创建交易
     *
     * @param total
     * @param cancelUrl
     * @param successUrl
     * @param orderNO
     * @param customMsg
     * @return
     * @throws PayPalRESTException
     */
    Payment createPayment(Double total,
                          String cancelUrl,
                          String successUrl,
                          String orderNO, String customMsg) throws PayPalRESTException;

    /**
     * 创建交易
     *
     * @param total
     * @param currency
     * @param method
     * @param intent
     * @param description
     * @param cancelUrl
     * @param successUrl
     * @param orderNO
     * @param customMsg
     * @return
     * @throws PayPalRESTException
     */
    Payment createPayment(
            Double total,
            String currency,
            PayPalPaymentMethodEnum method,
            PayPalPaymentIntentEnum intent,
            String description,
            String cancelUrl,
            String successUrl,
            String orderNO, String customMsg) throws PayPalRESTException;

    /**
     * 执行交易（回调时候执行）
     *
     * @param paymentId
     * @param payerId
     * @return
     * @throws PayPalRESTException
     */
    Payment executePayment(String paymentId, String payerId) throws PayPalRESTException;

    String getRandomUUID();

    /**
     * 退款处理
     * @param saleId
     * @param amount
     * @return
     * @throws IOException
     */
    CommonResult refund(String saleId, Double amount) throws IOException;
}
