package com.macro.mall.controller;

import cn.hutool.core.util.StrUtil;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.OmsOrderDetail;
import com.macro.mall.dto.SyncOrderParam;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.service.OmsOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.controller
 * @date:2021-05-14
 */
@RestController
@Api(tags = "XmsSyncDataController", description = "同步现有的数据接口")
@RequestMapping("/syncData")
@Slf4j
public class XmsSyncDataController {


    @Autowired
    private OmsOrderService orderService;

    @ApiOperation("获取客户的订单总数")
    @RequestMapping(value = "/getOrderCountByUserInfo", method = RequestMethod.GET)
    public CommonResult getOrderCountByUserInfo(SyncOrderParam syncOrderParam) {

        Assert.notNull(syncOrderParam, "syncOrderParam null");
        Assert.isTrue(null != syncOrderParam.getMemberId() && syncOrderParam.getMemberId() > 0, "memberId null");
        Assert.isTrue(StrUtil.isNotBlank(syncOrderParam.getUserName()), "userName null");

        try {
            long listCount = this.orderService.listCount(syncOrderParam);
            return CommonResult.success(listCount);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getOrderCountByUserInfo,syncOrderParam[{}],error:", syncOrderParam, e);
            return CommonResult.failed("getOrderCountByUserInfo error!");
        }
    }


    @ApiOperation("获取客户的订单")
    @RequestMapping(value = "/orderList", method = RequestMethod.GET)
    public CommonResult orderList(SyncOrderParam syncOrderParam) {

        Assert.notNull(syncOrderParam, "syncOrderParam null");
        Assert.isTrue(null != syncOrderParam.getMemberId() && syncOrderParam.getMemberId() > 0, "memberId null");
        Assert.isTrue(StrUtil.isNotBlank(syncOrderParam.getUserName()), "userName null");
        Assert.isTrue(null != syncOrderParam.getPageNum() && syncOrderParam.getPageNum() > 0, "pageNum null");
        Assert.isTrue(null != syncOrderParam.getPageSize() && syncOrderParam.getPageSize() > 0, "pageSize null");

        try {
            List<OmsOrder> list = this.orderService.list(syncOrderParam);
            return CommonResult.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("orderList,syncOrderParam[{}],error:", syncOrderParam, e);
            return CommonResult.failed("query list error!");
        }
    }

    @ApiOperation("获取客户的订单详情")
    @RequestMapping(value = "/orderDetails/{orderNo}", method = RequestMethod.GET)
    public CommonResult orderDetails(@PathVariable("orderNo") Long orderNo) {

        try {
            OmsOrderDetail orderDetailResult = orderService.detail(orderNo);
            return CommonResult.success(orderDetailResult);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("orderDetails,orderNo[{}],error:", orderNo, e);
            return CommonResult.failed("query list error!");
        }
    }

}
