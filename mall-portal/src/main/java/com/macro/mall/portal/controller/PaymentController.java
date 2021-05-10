package com.macro.mall.portal.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.PayPalParam;
import com.macro.mall.portal.domain.SiteEnum;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.util.OrderUtils;
import com.macro.mall.portal.util.PayUtil;
import com.paypal.api.payments.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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
    private UmsMemberService umsMemberService;
    @Autowired
    private PayUtil payUtil;
    @Autowired
    private OrderUtils orderUtils;


    @ApiOperation(value = "调用paypal支付", notes = "支付")
    @PostMapping("/paypal")
    public CommonResult paypal(HttpServletRequest request, @RequestParam("orderNo") String orderNo, @RequestParam("totalAmount") Double totalAmount) {
        Assert.isTrue(StrUtil.isNotBlank(orderNo), "orderNo null");
        Assert.isTrue(null != totalAmount && totalAmount > 0, "totalAmount null");

        try {
            UmsMember currentMember = this.umsMemberService.getCurrentMember();
            PayPalParam payPalParam = payUtil.getPayPalParam(request, currentMember.getId(), orderNo, totalAmount);
            payUtil.insertPayment(currentMember.getUsername(), currentMember.getId(), orderNo, totalAmount, 0, "", "单独支付");
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
    public CommonResult executePaymentApiCallAndRenderConfirmation(HttpServletRequest request, String paymentId, String PayerID, String token) throws IOException {
        HttpSession session = request.getSession(true);

        //paymentId = request.getParameter("paymentId");
        //payer_id = request.getParameter("PayerID");
        JsonObject paymentData = this.payUtil.getExpressCheckoutJsonDataForDoPayment(session, PayerID, false);
        System.err.println("---------The http post data:----paymentData:" + paymentData);
        Payment payment;
        try {
            UmsMember currentMember = this.umsMemberService.getCurrentMember();

            System.err.println("do success");
            CommonResult commonResult = this.payUtil.execute(paymentId, PayerID, SiteEnum.SOURCING);

            if (CommonResult.SUCCESS == commonResult.getCode()) {
                System.err.println("---------success Data:" + JSONObject.toJSONString(commonResult));
                //获取数据并且更新订单状态
                payment = JSONObject.parseObject(commonResult.getData().toString(), Payment.class);
                Map<String, String> payerInfoMap = this.payUtil.getCommonPayerInfoFields(payment);
                //获取订单号
                String itemNumber = payerInfoMap.get("itemId");
                // 支付成功
                if (payment.getState().equals("approved")) {
                    //获取付款金额
                    String amount = payerInfoMap.get("totalAmount");
                    this.payUtil.insertPayment(currentMember.getUsername(), currentMember.getId(), itemNumber, Double.parseDouble(amount), 0, paymentId, "支付回调日志,PayerID:" + PayerID + ",token:" + token);
                    // 更新订单数据 更新库存状态
                    this.orderUtils.paySuccessUpdate(itemNumber, 1);
                } else {
                    this.orderUtils.paySuccessUpdate(itemNumber, 0);
                    log.error("paypalApiCalAndConfirm Bad response：" + payment.toJSON());
                }
            } else {
                // 支付失败
                //增加支付失败记录
                Map<String, String> failure = new HashMap<String, String>(10);
                failure.put("itemNumber", "0");
                failure.put("paySID", paymentId);
                failure.put("per_payprice", session.getAttribute("totalAmountDefault") == null ? "0" : session.getAttribute("totalAmountDefault").toString());
                failure.put("description", "executePaymentApiCallAndRenderConfirmation:" + paymentData.toString());
                failure.put("useBalance", "0");
                failure.put("payment_method_nonce", "0");
                failure.put("payerId", PayerID);
                failure.put("message", commonResult.getMessage());
                String message = StrUtil.isBlank(failure.get("message")) ? "" : (failure.get("message").toString());
                request.setAttribute("error_message", message);
                //记录错误信息
                log.error("paypalApiCalAndConfirm failureInfo[{}]", failure);

                String description = String.valueOf(session.getAttribute("description") == null ? "" : session.getAttribute("description"));
                if (!"".equals(description) && description.split("@").length >= 7) {
                    String[] array = description.split("@");
                    /*Payment pay = new Payment();
                    pay.setUserid(Integer.parseInt(array[0]));
                    pay.setOrderdesc("支付回调：支付失败！");
                    pay.setOrderid(array[6]);
                    failure.put("itemNumber", array[6]);
                    pay.setPaystatus(0);
                    paymentService.addPayment(pay);*/
                    this.orderUtils.paySuccessUpdate(array[6], 0);
                }
            }

            return commonResult;
        } catch (Exception e) {
            log.error("error", e);
            return CommonResult.failed(e.getMessage());
        }
    }


}
