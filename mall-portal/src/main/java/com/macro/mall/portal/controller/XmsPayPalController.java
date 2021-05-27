package com.macro.mall.portal.controller;

import com.google.gson.Gson;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.enums.SiteEnum;
import com.macro.mall.portal.enums.PayPalPaymentIntentEnum;
import com.macro.mall.portal.enums.PayPalPaymentMethodEnum;
import com.macro.mall.portal.service.PayPalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author jack.luo
 * @create 2020/4/7 18:07
 * Description
 */
@RestController
@Slf4j
@Api("PayPal支付接口")
@RequestMapping("/paypal")
public class XmsPayPalController {

    private PayPalService payPalService;

    @Autowired
    public XmsPayPalController(PayPalService payPalService) {

        this.payPalService = payPalService;
    }

    @PostMapping("/{site}/create")
    @ApiOperation("支付创建")
    public CommonResult createPayment(@PathVariable(value = "site") SiteEnum site,
                                      @RequestParam Double total, @RequestParam String orderNo, @RequestParam(value = "customMsg", required = false, defaultValue = "") String customMsg, @RequestParam(value = "cancelUrlType", defaultValue = "0") int cancelUrlType,
                                      @RequestParam(value = "successUrl", required = false, defaultValue = "") String successUrl,
                                      @RequestParam(value = "cancelUrl", required = false, defaultValue = "") String cancelUrl) {

        try {
            if(StringUtils.isEmpty(successUrl)){
                successUrl = site.getUrl() + "/doPayment/pay";
            }
            if(StringUtils.isEmpty(cancelUrl)){
                if(cancelUrlType==1){
                    //返回购物车
                    cancelUrl = site.getUrl() + "/Goods/getShopCar?from=pay";
                }else{
                    //返回个人中心
                    cancelUrl = site.getUrl() + "/myaccount";
                }
            }

            Payment payment = payPalService.createPayment(total,
                    "USD",
                    PayPalPaymentMethodEnum.paypal,
                    PayPalPaymentIntentEnum.sale,
                    "",
                    cancelUrl,
                    successUrl,
                    orderNo, customMsg);
            String strApprovalUrl=null;
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    strApprovalUrl=links.getHref();
                    break;
                }
            }
            return CommonResult.success(strApprovalUrl);
        } catch (Exception e) {
            log.error("createPayment()", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @PostMapping("/{site}/execute")
    @ApiOperation("支付执行")
    public CommonResult execute(@PathVariable(value = "site") SiteEnum site, @RequestParam String paymentId, @RequestParam String payerId) {

        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                return CommonResult.success(new Gson().toJson(payment));
            } else {
                return CommonResult.failed(payment.getState());
            }
        } catch (Exception e) {
            log.error("execute()", e);
            return CommonResult.failed(e.getMessage());
        }
    }
}
