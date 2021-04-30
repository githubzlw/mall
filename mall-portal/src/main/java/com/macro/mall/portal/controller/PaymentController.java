package com.macro.mall.portal.controller;

import cn.hutool.core.util.StrUtil;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.PayPalParam;
import com.macro.mall.portal.domain.SiteEnum;
import com.macro.mall.portal.util.Md5Util;
import com.macro.mall.portal.util.PayUtil;
import com.paypal.api.payments.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonObject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.controller
 * @date:2021-04-29
 */
@Api(tags = "PaymentController", description = "支付调用接口")
@RestController
@Slf4j
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PayUtil payUtil;

    @ApiOperation(value = "调用paypal支付", notes = "支付")
    @PostMapping("/paypal")
    public CommonResult paypal(@RequestParam("orderNo") String orderNo, @RequestParam("totalAmount") Double totalAmount) {
        Assert.isTrue(StrUtil.isNotBlank(orderNo), "orderNo null");
        Assert.isTrue(null != totalAmount && totalAmount > 0, "totalAmount null");

        PayPalParam payPalParam = new PayPalParam();

        String paySID = UUID.randomUUID().toString();

        String appConfig_paypal_business = "584JZVFU6PPVU";
        int userid = 15937;
        String md = appConfig_paypal_business + userid + orderNo + totalAmount;
        String sign = Md5Util.encoder(md);
        String payflag = "O";
        String isBalance = "0";
        double credit = 0;
        int dropshipflag = 0;
        double productCost = totalAmount - 10;
        double coupon_discount = 0;

        String customMsg = userid + "@" + paySID + "@" + sign + "@" + payflag + "@" + isBalance + "@" + credit + "@" + orderNo + "@" + dropshipflag + "@" + productCost + "@" + coupon_discount;
        try {
            payPalParam.setOrderNo(orderNo);
            payPalParam.setTotalAmount(totalAmount);
            payPalParam.setSiteName(SiteEnum.SOURCING.getName());
            payPalParam.setCancelUrl("http://192.168.1.67:8087/myaccount");
            payPalParam.setCancelUrlType(1);
            payPalParam.setSuccessUrl("http://192.168.1.67:8085/payment/paypalApiCalAndConfirm");
            payPalParam.setCustomMsg(customMsg);
            CommonResult commonResult = payUtil.getPayPalRedirectUtlByPayInfo(payPalParam);
            return commonResult;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("paypal,orderNo[{}],totalAmount[{}],error:", orderNo, totalAmount, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation(value = "paypal支付支付成功后回调", notes = "支付")
    @GetMapping("/paypalApiCalAndConfirm")
    public CommonResult executePaymentApiCallAndRenderConfirmation(HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession(true);

        String paymentId = "", access_token = "", payer_id = "";
        paymentId = request.getParameter("paymentId");
        payer_id = request.getParameter("PayerID");
        JsonObject paymentData = payUtil.getExpressCheckoutJsonDataForDoPayment(session, payer_id, false);
        System.err.println("---------The http post data:----paymentData:" + paymentData);
        Payment payment = new Payment();
        try {
            System.err.println("do success");
            CommonResult commonResult = payUtil.execute(paymentId, payer_id, SiteEnum.SOURCING);
            return commonResult;
        } catch (Exception e) {
            log.error("error", e);
            return CommonResult.failed(e.getMessage());
        }
    }


}
