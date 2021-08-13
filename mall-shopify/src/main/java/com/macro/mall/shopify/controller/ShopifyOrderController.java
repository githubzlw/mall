package com.macro.mall.shopify.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsShopifyOrderDetails;
import com.macro.mall.entity.XmsShopifyOrderinfo;
import com.macro.mall.shopify.pojo.FulfillmentParam;
import com.macro.mall.shopify.pojo.FulfillmentStatusEnum;
import com.macro.mall.shopify.pojo.LogisticsCompanyEnum;
import com.macro.mall.shopify.util.ShopifyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;


/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.control
 * @date:2019/11/28
 */
@Slf4j
@RestController
@RequestMapping("/shopify")
@Api(tags = "shopify订单调用接口")
public class ShopifyOrderController {


    @Autowired
    private ShopifyUtils shopifyUtils;

    @PostMapping("/getCountryByShopifyName")
    @ApiOperation("根据shopifyName获取国家数据")
    public CommonResult getCountryByShopifyName(String shopifyName) {
        Assert.isTrue(StrUtil.isNotEmpty(shopifyName), "shopifyName null");
        try {
            int total = this.shopifyUtils.getCountryByShopifyName(shopifyName);
            return CommonResult.success(total);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getCountryByShopifyName, shopifyName[{}],error:", shopifyName, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping("/getOrdersByShopifyName")
    @ApiOperation("根据shopifyName获取订单数据")
    public CommonResult getOrdersByShopifyName(@RequestParam("shopifyNameList") List<String> shopifyNameList) {
        Assert.isTrue(CollectionUtil.isNotEmpty(shopifyNameList), "shopifyNameList null");
        try {
            int total = this.shopifyUtils.getOrdersByShopifyName(shopifyNameList);
            return CommonResult.success(total);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getOrdersByShopifyName, shopifyNameList[{}],error:", shopifyNameList, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping("/createFulfillment")
    @ApiOperation("创建履行订单")
    public CommonResult createFulfillment(FulfillmentParam fulfillmentParam) {

        Assert.notNull(fulfillmentParam, "fulfillmentParam is null");
        Assert.isTrue(StrUtil.isNotBlank(fulfillmentParam.getShopifyName()), "shopifyName is null");
        Assert.isTrue(null != fulfillmentParam.getOrderNo() && fulfillmentParam.getOrderNo() > 0, "orderNo is null");
        Assert.isTrue(StrUtil.isNotBlank(fulfillmentParam.getTrackingNumber()), "trackingNumber is null");
        Assert.isTrue(StrUtil.isNotBlank(fulfillmentParam.getTrackingCompany()), "trackingCompany is null");


        try {
            // 获取订单fulfillmentservice的ID


            LogisticsCompanyEnum anElse = Arrays.stream(LogisticsCompanyEnum.values()).filter(e -> e.getName().equalsIgnoreCase(fulfillmentParam.getTrackingCompany())).findFirst().orElse(null);
            if (null == anElse) {
                return CommonResult.failed("Cannot match company name");
            }


            List<XmsShopifyOrderinfo> xmsShopifyOrderinfos = this.shopifyUtils.queryListByOrderNo(fulfillmentParam.getOrderNo());
            if (CollectionUtil.isEmpty(xmsShopifyOrderinfos)) {
                return CommonResult.failed("Cannot match OrderNo");
            }

            List<XmsShopifyOrderDetails> detailsList = this.shopifyUtils.queryDetailsListByOrderNo(fulfillmentParam.getOrderNo());
            if (CollectionUtil.isEmpty(detailsList)) {
                return CommonResult.failed("Cannot match Details List");
            }

            String updateOrder = this.shopifyUtils.createFulfillmentOrders(xmsShopifyOrderinfos.get(0), detailsList, fulfillmentParam, anElse);
            if (StrUtil.isNotEmpty(updateOrder)) {
                return CommonResult.success(JSONObject.parseObject(updateOrder));
            }
            return CommonResult.success(updateOrder);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("createFulfillment fulfillmentParam:[{}],error:", fulfillmentParam, e);
            return CommonResult.failed("fulfillmentParam:[{" + JSONObject.toJSONString(fulfillmentParam) + "}]" + ",error:" + e.getMessage());
        }
    }


    @RequestMapping(value = "/fulfillmentCallback", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("创建运单服务回调")
    public CommonResult fulfillmentCallback(HttpServletRequest request) {
        return CommonResult.success(request.getParameterMap());
    }


    @PostMapping("/putOrders")
    @ApiOperation("根据shopifyName更新订单状态")
    public CommonResult updateOrder(@ApiParam(name = "shopifyName", value = "shopify店铺名", required = true)
                                    @RequestParam(value = "shopifyName") String shopifyName,
                                    @ApiParam(name = "orderNo", value = "订单号", required = true)
                                    @RequestParam(value = "orderNo") Long orderNo,
                                    @ApiParam(name = "fulfillment_status", value = "订单状态", required = true)
                                    @RequestParam(value = "fulfillment_status") String fulfillment_status) {

        Assert.notNull(shopifyName, "shopifyName is null");
        Assert.notNull(orderNo, "orderNo is null");
        Assert.notNull(fulfillment_status, "fulfillment_status is null");

        FulfillmentStatusEnum anEnum = Arrays.stream(FulfillmentStatusEnum.values()).filter(e -> e.toString().toLowerCase().equalsIgnoreCase(fulfillment_status)).findFirst().orElse(null);

        Assert.notNull(anEnum, "fulfillment_status is null");

        try {

            String updateOrder = this.shopifyUtils.updateOrder(orderNo, shopifyName, anEnum);
            if (StrUtil.isNotEmpty(updateOrder)) {
                return CommonResult.success(JSONObject.parseObject(updateOrder));
            }
            return CommonResult.success(updateOrder);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("shopifyName:" + shopifyName + ",putOrders orderNo:" + orderNo + ",fulfillment_status:" + fulfillment_status + ",error:", e);
            return CommonResult.failed("shopifyName:" + shopifyName + ",putOrders orderNo:" + orderNo + ",fulfillment_status:" + fulfillment_status + ",error:" + e.getMessage());
        }
    }


    @PostMapping("/updateFulfillmentOrders")
    @ApiOperation("更新履行订单的fulfill_at时间")
    public CommonResult updateFulfillmentOrders(@ApiParam(name = "shopifyName", value = "shopify店铺名", required = true)
                                                @RequestParam(value = "shopifyName") String shopifyName,
                                                @ApiParam(name = "orderNo", value = "订单号", required = true)
                                                @RequestParam(value = "orderNo") Long orderNo,
                                                @ApiParam(name = "new_fulfill_at", value = "标记为准备履行的时间", required = true)
                                                @RequestParam(value = "new_fulfill_at") String new_fulfill_at) {

        Assert.notNull(shopifyName, "shopifyName is null");
        Assert.notNull(orderNo, "orderNo is null");
        Assert.notNull(new_fulfill_at, "new_fulfill_at is null");


        try {

            String updateOrder = this.shopifyUtils.updateFulfillmentOrders(orderNo, shopifyName, new_fulfill_at);
            if (StrUtil.isNotEmpty(updateOrder)) {
                return CommonResult.success(JSONObject.parseObject(updateOrder));
            }
            return CommonResult.success(updateOrder);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("shopifyName:" + shopifyName + ",updateFulfillmentOrders orderNo:" + orderNo + ",new_fulfill_at:" + new_fulfill_at + ",error:", e);
            return CommonResult.failed("shopifyName:" + shopifyName + ",updateFulfillmentOrders orderNo:" + orderNo + ",new_fulfill_at:" + new_fulfill_at + ",error:" + e.getMessage());
        }
    }

}
