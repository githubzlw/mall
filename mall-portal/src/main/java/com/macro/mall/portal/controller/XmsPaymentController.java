package com.macro.mall.portal.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.cache.RedisUtil;
import com.macro.mall.portal.domain.PayPalParam;
import com.macro.mall.common.enums.SiteEnum;
import com.macro.mall.portal.enums.PayFromEnum;
import com.macro.mall.portal.enums.PayStatusEnum;
import com.macro.mall.portal.enums.PayTypeEnum;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.enums.OrderPrefixEnum;
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
import java.math.BigDecimal;
import java.util.*;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.controller
 * @date:2021-04-29
 */
@Api(tags = "XmsPaymentController", description = "支付调用接口")
@RestController
@Slf4j
@RequestMapping("/payment")
public class XmsPaymentController {

    @Autowired
    private UmsMemberService umsMemberService;
    @Autowired
    private PayUtil payUtil;
    @Autowired
    private OrderUtils orderUtils;
    @Autowired
    private RedisUtil redisUtil;


    @ApiOperation(value = "正常订单调用paypal支付", notes = "支付")
    @PostMapping("/paypal")
    public CommonResult paypal(HttpServletRequest request, @RequestParam("orderNo") String orderNo, @RequestParam("totalAmount") Double totalAmount, @RequestParam(value = "payFrom", defaultValue = "0") Integer payFrom) {
        Assert.isTrue(StrUtil.isNotBlank(orderNo), "orderNo null");
        Assert.isTrue(null != totalAmount && totalAmount > 0, "totalAmount null");

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {

            PayFromEnum payFromEnum = Arrays.stream(PayFromEnum.values()).filter(e -> e.getCode() == payFrom).findFirst().orElse(null);
            if (null == payFromEnum) {
                return CommonResult.failed("PayFrom error");
            }


            // 判断订单是否完成支付
            boolean isPay = this.orderUtils.checkPayStatusByOrderNo(orderNo);
            if (isPay) {
                return CommonResult.failed("This order has been paid");
            }

            PayPalParam payPalParam = this.payUtil.getPayPalParam(request, currentMember.getId(), orderNo, totalAmount);

            //支付日志
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setOrderSn(orderNo);
            omsOrder.setTotalAmount(new BigDecimal(totalAmount));
            omsOrder.setNote("订单支付");
            omsOrder.setPayType(0);
            omsOrder.setStatus(0);
            this.payUtil.insertPaymentLog(currentMember, "", payFromEnum, omsOrder);

            this.payUtil.insertPayment(currentMember, orderNo, totalAmount, PayStatusEnum.PENDING, "", "订单支付", PayTypeEnum.PAYPAL, payFromEnum);
            if (payFromEnum == PayFromEnum.PURCHASE_INVENTORY) {
                payPalParam.setSuccessUrlType("1");
            }
            payPalParam.setMemberId(currentMember.getId());
            CommonResult commonResult = this.payUtil.getPayPalRedirectUtlByPayInfo(payPalParam, this.redisUtil);
            return commonResult;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("paypal,orderNo[{}],totalAmount[{}],error:", orderNo, totalAmount, e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "充值余额调用paypal支付", notes = "支付")
    @PostMapping("/topUpBalance")
    public CommonResult topUpBalance(HttpServletRequest request, @RequestParam("totalAmount") Double totalAmount, @RequestParam(value = "payFrom", defaultValue = "0") Integer payFrom) {
        Assert.isTrue(null != totalAmount && totalAmount > 0, "totalAmount null");

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        String orderNo = null;
        try {

            PayFromEnum payFromEnum = Arrays.stream(PayFromEnum.values()).filter(e -> e.getCode() == payFrom).findFirst().orElse(null);
            if (null == payFromEnum) {
                return CommonResult.failed("PayFrom error");
            }


            orderNo = this.orderUtils.getOrderNoByRedis(OrderPrefixEnum.Balance.getCode());
            // 生成订单信息
            this.orderUtils.generateBalanceOrder(orderNo, totalAmount, currentMember.getId(), currentMember.getUsername());
            PayPalParam payPalParam = payUtil.getPayPalParam(request, currentMember.getId(), orderNo, totalAmount);

            //支付日志
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setOrderSn(orderNo);
            omsOrder.setTotalAmount(new BigDecimal(totalAmount));
            omsOrder.setNote("充值余额");
            omsOrder.setPayType(0);
            omsOrder.setStatus(0);
            this.payUtil.insertPaymentLog(currentMember, "", payFromEnum, omsOrder);

            //this.payUtil.insertPayment(currentMember, orderNo, totalAmount, PayStatusEnum.PENDING, "", "余额支付", PayTypeEnum.PAYPAL, payFromEnum);
            payPalParam.setMemberId(currentMember.getId());
            CommonResult commonResult = this.payUtil.getPayPalRedirectUtlByPayInfo(payPalParam, this.redisUtil);
            return commonResult;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("paypal,orderNo[{}],totalAmount[{}],error:", orderNo, totalAmount, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation(value = "paypal支付支付成功后回调", notes = "支付")
    @GetMapping("/paypalApiCalAndConfirm")
    public CommonResult executePaymentApiCallAndRenderConfirmation(HttpServletRequest request, String paymentId, String PayerID, String token, Integer payFrom) throws IOException {
        HttpSession session = request.getSession(true);

        //paymentId = request.getParameter("paymentId");
        //payer_id = request.getParameter("PayerID");
        JsonObject paymentData = this.payUtil.getExpressCheckoutJsonDataForDoPayment(session, PayerID, false);
        System.err.println("---------The http post data:----paymentData:" + paymentData);
        Payment payment;
        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {

            if (null == payFrom) {
                payFrom = 0;
            }
            Integer finalPayFrom = payFrom;
            PayFromEnum payFromEnum = Arrays.stream(PayFromEnum.values()).filter(e -> e.getCode() == finalPayFrom).findFirst().orElse(null);
            if (null == payFromEnum) {
                payFromEnum = PayFromEnum.NONE;
            }

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
                    this.insertPayInfo(payerInfoMap, itemNumber, PayerID, token, currentMember, paymentId, payFromEnum);
                } else {
                    System.err.println(itemNumber + ",pay result: " + payment.toJSON());
                    this.orderUtils.paySuccessUpdate(itemNumber, 0);
                    log.error("paypalApiCalAndConfirm Bad response：" + payment.toJSON());
                }

                Object val = this.redisUtil.hmgetObj(OrderUtils.PAY_USER_ID, String.valueOf(currentMember.getId()));
                if (null != val && itemNumber.equalsIgnoreCase(val.toString())) {
                    this.redisUtil.hdel(OrderUtils.PAY_USER_ID, String.valueOf(currentMember.getId()));
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

    public void insertPayInfo(Map<String, String> payerInfoMap, String itemNumber, String PayerID, String token, UmsMember currentMember, String paymentId, PayFromEnum payFromEnum) {
        // 将paymentId的数据和订单数据放入redis中，防止重复支付

        synchronized (currentMember.getId()) {
            //获取付款金额
            String amount = payerInfoMap.get("totalAmount");

            //支付日志
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setOrderSn(itemNumber);
            omsOrder.setTotalAmount(new BigDecimal(amount));
            omsOrder.setNote("支付回调日志,PayerID:" + PayerID + ",token:" + token);
            omsOrder.setPayType(0);
            omsOrder.setStatus(0);
            this.payUtil.insertPaymentLog(currentMember, paymentId, payFromEnum, omsOrder);

            // 插入支付表
            this.payUtil.insertPayment(currentMember, itemNumber, Double.parseDouble(amount), PayStatusEnum.SUCCESS, paymentId, "支付回调日志,PayerID:" + PayerID + ",token:" + token, PayTypeEnum.PAYPAL, payFromEnum);
            // 更新订单数据 更新库存状态
            this.orderUtils.paySuccessUpdate(itemNumber, 1);

            // BL开头的订单，更新客户余额
            if (itemNumber.indexOf(OrderPrefixEnum.Balance.getCode()) == 0) {
                this.payUtil.payBalance(Double.parseDouble(amount), currentMember, 1, itemNumber, paymentId, payFromEnum);
            } else {
                this.payUtil.payBalanceByOrderNo(itemNumber, currentMember.getId(), payFromEnum);
            }
        }
    }

}
