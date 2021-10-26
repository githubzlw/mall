package com.macro.mall.portal.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.cache.RedisUtil;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.enums.PayFromEnum;
import com.macro.mall.portal.service.OmsPortalOrderService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.util.PayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单管理Controller
 * Created by macro on 2018/8/30.
 */
@Controller
@Api(tags = "OmsPortalOrderController", description = "订单管理")
@RequestMapping("/order")
public class OmsPortalOrderController {
    @Autowired
    private OmsPortalOrderService portalOrderService;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UmsMemberService umsMemberService;
    @Autowired
    private PayUtil payUtil;

    private static final String SOURCING_BEFORE_ORDER = "sourcing:beForeOrder:";

    @ApiOperation("根据购物车信息生成确认单信息")
    @RequestMapping(value = "/generateConfirmOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<ConfirmOrderResult> generateConfirmOrder(@RequestBody List<Long> cartIds) {
        ConfirmOrderResult confirmOrderResult = portalOrderService.generateConfirmOrder(cartIds);
        return CommonResult.success(confirmOrderResult);
    }


    @ApiOperation("保存购物车信息生成Sourcing预览信息")
    @RequestMapping(value = "/beForeSourcingOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult beForeSourcingOrder(SourcingOrderParam orderParam) {

        redisUtil.hset(SOURCING_BEFORE_ORDER, umsMemberService.getCurrentMember().getId().toString(), JSONObject.toJSONString(orderParam), 60 * 60 * 24 * 7);
        return CommonResult.success(orderParam, "success");
    }

    @ApiOperation("获取购物车信息生成Sourcing预览信息")
    @RequestMapping(value = "/AfterSourcingOrder", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult AfterSourcingOrder() {
        String hget = redisUtil.hget(SOURCING_BEFORE_ORDER, umsMemberService.getCurrentMember().getId().toString());
        if (StrUtil.isNotBlank(hget)) {
            SourcingOrderParam orderParam = JSONObject.parseObject(hget, SourcingOrderParam.class);
            return CommonResult.success(orderParam, "success");
        }
        return CommonResult.success(null);
    }

    @ApiOperation("根据购物车信息生成Sourcing订单")
    @RequestMapping(value = "/generateSourcingOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult generateSourcingOrder(SourcingOrderParam orderParam) {
        Map<String, Object> result = portalOrderService.generateSourcingOrder(orderParam);
        return CommonResult.success(result, "下单成功");
    }

    @ApiOperation("根据购物车信息生成订单")
    @RequestMapping(value = "/generateOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult generateOrder(@RequestBody OrderParam orderParam) {
        Map<String, Object> result = portalOrderService.generateOrder(orderParam);
        return CommonResult.success(result, "下单成功");
    }

    @ApiOperation("用户支付成功的回调")
    @RequestMapping(value = "/paySuccess", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult paySuccess(@RequestParam Long orderId,@RequestParam Integer payType) {
        Integer count = portalOrderService.paySuccess(orderId,payType);
        return CommonResult.success(count, "支付成功");
    }

    @ApiOperation("自动取消超时订单")
    @RequestMapping(value = "/cancelTimeOutOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult cancelTimeOutOrder() {
        portalOrderService.cancelTimeOutOrder();
        return CommonResult.success(null);
    }

    @ApiOperation("取消单个超时订单")
    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult cancelOrder(Long orderId) {
        portalOrderService.sendDelayMessageCancelOrder(orderId);
        return CommonResult.success(null);
    }

    @ApiOperation("按状态分页获取用户订单列表")
    @ApiImplicitParam(name = "status", value = "订单状态：0->待付款；1->采购；2->入库；3->已发货；4->已完结；5->已经付款； -1/6->取消订单",
            defaultValue = "-1", allowableValues = "-2,-1,0,1,2,3,4,5,6", paramType = "query", dataType = "int")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsOrderDetail>> list(@RequestParam Integer status,
                                                         @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                         @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                                                         @RequestParam(required = false, defaultValue = "") String productName) {
        CommonPage<OmsOrderDetail> orderPage = portalOrderService.list(status, pageNum, pageSize, productName);
        return CommonResult.success(orderPage);
    }

    @ApiOperation("获取待支付的订单总数")
    @RequestMapping(value = "/processPaymentCount", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Long> processPaymentCount() {
        long paymentCount = portalOrderService.processPaymentCount();
        return CommonResult.success(paymentCount);
    }


    @ApiOperation("根据ID获取订单详情")
    @RequestMapping(value = "/detail/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsOrderDetail> detail(@PathVariable Long orderId) {
        OmsOrderDetail orderDetail = portalOrderService.detail(orderId);
        return CommonResult.success(orderDetail);
    }

    @ApiOperation("用户取消订单")
    @RequestMapping(value = "/cancelUserOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult cancelUserOrder(Long orderId) {
        portalOrderService.cancelOrder(orderId);
        return CommonResult.success(null);
    }

    @ApiOperation("用户确认收货")
    @RequestMapping(value = "/confirmReceiveOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult confirmReceiveOrder(Long orderId) {
        portalOrderService.confirmReceiveOrder(orderId);
        return CommonResult.success(null);
    }

    @ApiOperation("用户删除订单")
    @RequestMapping(value = "/deleteOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteOrder(Long orderId) {
        portalOrderService.deleteOrder(orderId);
        return CommonResult.success(null);
    }


    @ApiOperation("订单BeforePay")
    @RequestMapping(value = "/beforePayOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult beforePayOrder(Long orderId, HttpServletRequest request) {

        Assert.isTrue(null != orderId && orderId > 0, "orderId null");
        UmsMember currentMember = umsMemberService.getCurrentMember();
        synchronized (currentMember.getId()) {

            OmsOrderDetail detail = portalOrderService.detail(orderId);
            if (null == detail || null == detail.getId() || detail.getId() == 0) {
                return CommonResult.failed("no this order");
            }
            if(0 != detail.getStatus()){
                return CommonResult.failed("This order has been paid or cancelled");
            }

            double totalAmount = 0; // 总金额
            double payAmount = 0; // PayPal支付
            double balanceAmount = 0; // 余额支付
            GenerateOrderResult orderResult = new GenerateOrderResult();
            // 判断已经做过处理的，不再进行计算
            if (detail.getStatus() == 0 && null != detail.getPaymentTime()) {
                totalAmount = detail.getPayAmount().doubleValue() + detail.getBalanceAmount(); // 总金额
                balanceAmount = 0; // 余额支付
            } else {
                totalAmount = detail.getPayAmount().doubleValue(); // 总金额
                balanceAmount = 0; // 余额支付
            }

            UmsMember umsMember = umsMemberService.getById(currentMember.getId());
            Double tempBalance = umsMember.getBalance();
            // 存在余额则部分余额支付或者余额支付
            if (null != tempBalance && tempBalance > 0) {

                if (tempBalance >= totalAmount) {
                    payAmount = 0;
                    balanceAmount = totalAmount;
                } else {
                    balanceAmount = tempBalance;
                    payAmount = totalAmount - balanceAmount;
                }
            } else {
                payAmount = totalAmount;
            }
            detail.setPayAmount(new BigDecimal(payAmount));
            detail.setBalanceAmount(balanceAmount);
            detail.setPaymentTime(new Date());
            this.portalOrderService.updateBalanceRecode(detail);

            orderResult.setOrderNo(detail.getOrderSn());
            orderResult.setBalanceAmount(balanceAmount);
            orderResult.setPayAmount(payAmount);
            orderResult.setTotalAmount(totalAmount);
            orderResult.setProductCost(detail.getTotalAmount().floatValue());
            orderResult.setTotalFreight(detail.getFreightAmount().doubleValue());

            return this.payUtil.beforePayAndPay(orderResult, currentMember, request, PayFromEnum.SOURCING_ORDER, this.redisUtil);
        }


    }



    @ApiOperation("获取订单信息")
    @RequestMapping(value = "/getOrderInfo/{orderNo}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getOrderInfoByOrderNo(@PathVariable("orderNo") String orderNo) {
        OmsOrder omsOrder = this.portalOrderService.queryByOrderNo(orderNo);
        return CommonResult.success(omsOrder);
    }


}
