package com.macro.mall.shopify.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsShopifyOrderDetails;
import com.macro.mall.entity.XmsShopifyOrderinfo;
import com.macro.mall.shopify.cache.RedisUtil;
import com.macro.mall.shopify.pojo.FulfillmentParam;
import com.macro.mall.shopify.pojo.FulfillmentStatusEnum;
import com.macro.mall.shopify.pojo.LogisticsCompanyEnum;
import com.macro.mall.shopify.pojo.ShopifyOrderParam;
import com.macro.mall.shopify.util.AsyncTask;
import com.macro.mall.shopify.util.ShopifyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


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

    @Autowired
    private RedisUtil redisUtil;

    @Resource
    private AsyncTask asyncTask;

    @PostMapping("/getCountryByShopifyName")
    @ApiOperation("根据shopifyName获取国家数据")
    public CommonResult getCountryByShopifyName(String shopifyName, Long memberId) {
        Assert.isTrue(StrUtil.isNotEmpty(shopifyName), "shopifyName null");

        Map<String, String> map = new HashMap<>();
        try {
            Object val = this.redisUtil.hmgetObj(RedisUtil.GET_COUNTRY_BY_SHOPIFY_NAME, shopifyName);
            if (null != val && "success".equalsIgnoreCase(val.toString())) {
                return CommonResult.success("this shop is execute!!");
            }
            int total = this.shopifyUtils.getCountryByShopifyName(shopifyName, memberId);
            map.put(shopifyName, "success");
            this.redisUtil.hmset(RedisUtil.GET_COUNTRY_BY_SHOPIFY_NAME, map, 60);
            return CommonResult.success(total);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getCountryByShopifyName, shopifyName[{}],error:", shopifyName, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping("/getOrdersByShopifyName")
    @ApiOperation("根据shopifyName获取订单数据")
    public CommonResult getOrdersByShopifyName(String shopifyName, Long memberId) {
        Assert.isTrue(StrUtil.isNotBlank(shopifyName), "shopifyName null");
        try {
            Set<Long> orderList = this.shopifyUtils.getOrdersByShopifyName(shopifyName, memberId);
            if (CollectionUtil.isNotEmpty(orderList)) {
                this.asyncTask.getShopifyImgByList(orderList, shopifyName, memberId);
            }

            return CommonResult.success(orderList.size());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getOrdersByShopifyName, shopifyName[{}],error:", shopifyName, e);
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
        //Assert.isTrue(StrUtil.isNotBlank(fulfillmentParam.getTrackingCompany()), "trackingCompany is null");


        try {
            // 获取订单fulfillmentservice的ID
            /*LogisticsCompanyEnum anElse = Arrays.stream(LogisticsCompanyEnum.values()).filter(e -> e.getName().equalsIgnoreCase(fulfillmentParam.getTrackingCompany())).findFirst().orElse(null);
            if (null == anElse) {
                return CommonResult.failed("Cannot match company name");
            }*/

            LogisticsCompanyEnum anElse = LogisticsCompanyEnum.COMMON;

            List<XmsShopifyOrderinfo> xmsShopifyOrderinfos = this.shopifyUtils.queryListByOrderNo(fulfillmentParam.getOrderNo());
            if (CollectionUtil.isEmpty(xmsShopifyOrderinfos)) {
                return CommonResult.failed("Cannot match OrderNo");
            } else {
                xmsShopifyOrderinfos.clear();
            }

            List<XmsShopifyOrderDetails> detailsList = this.shopifyUtils.queryDetailsListByOrderNo(fulfillmentParam.getOrderNo());
            if (CollectionUtil.isEmpty(detailsList)) {
                return CommonResult.failed("Cannot match Details List");
            }
            String updateOrder = this.shopifyUtils.createFulfillmentOrders(detailsList, fulfillmentParam, anElse);

            detailsList.clear();
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


    @PostMapping("/putOrdersStatus")
    @ApiOperation("根据shopifyName更新订单状态")
    public CommonResult putOrdersStatus(@RequestBody ShopifyOrderParam orderParam) {

        Assert.isTrue(StrUtil.isNotBlank(orderParam.getShopifyName()), "shopifyName is null");
        Assert.isTrue(null != orderParam.getOrderNo() && orderParam.getOrderNo() > 0, "orderNo is null");
        Assert.isTrue(StrUtil.isNotBlank(orderParam.getFulfillmentStatus()), "fulfillmentStatus is null");

        FulfillmentStatusEnum anEnum = Arrays.stream(FulfillmentStatusEnum.values()).filter(e -> e.toString().toLowerCase().equalsIgnoreCase(orderParam.getFulfillmentStatus())).findFirst().orElse(null);
        Assert.notNull(anEnum, "fulfillment_status is null");
        try {
            String updateOrder = this.shopifyUtils.updateOrder(orderParam.getOrderNo(), orderParam.getShopifyName(), anEnum,orderParam.getMemberId());
            if (StrUtil.isNotEmpty(updateOrder)) {
                return CommonResult.success(JSONObject.parseObject(updateOrder));
            }
            return CommonResult.success(updateOrder);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("putOrdersStatus,orderParam[{}],error:", orderParam, e);
            return CommonResult.failed("putOrdersStatus," + JSONObject.toJSONString(orderParam) + ",error:" + e.getMessage());
        }
    }


    @PostMapping("/updateFulfillmentOrders")
    @ApiOperation("更新履行订单的fulfill_at时间")
    public CommonResult updateFulfillmentOrders(@RequestBody ShopifyOrderParam orderParam) {

        Assert.isTrue(StrUtil.isNotBlank(orderParam.getShopifyName()), "shopifyName is null");
        Assert.isTrue(null != orderParam.getOrderNo() && orderParam.getOrderNo() > 0, "orderNo is null");
        Assert.isTrue(StrUtil.isNotBlank(orderParam.getNewFulfillAt()), "fulfillmentStatus is null");

        try {
            String updateOrder = this.shopifyUtils.updateFulfillmentOrders(orderParam.getOrderNo(), orderParam.getShopifyName(), orderParam.getNewFulfillAt(), orderParam.getMemberId());
            if (StrUtil.isNotEmpty(updateOrder)) {
                return CommonResult.success(JSONObject.parseObject(updateOrder));
            }
            return CommonResult.success(updateOrder);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("updateFulfillmentOrders,orderParam[{}],error:", orderParam, e);
            return CommonResult.failed("updateFulfillmentOrders," + JSONObject.toJSONString(orderParam) + ",error:" + e.getMessage());
        }
    }


    @PostMapping("/getFulfillmentByShopifyName")
    @ApiOperation("获取运单数据")
    public CommonResult getFulfillmentByShopifyName(String shopifyName, String orders, Long memberId) {

        Assert.isTrue(StrUtil.isNotBlank(shopifyName), "shopifyName is null");
        Assert.isTrue(StrUtil.isNotBlank(orders), "orders is null");
        Assert.isTrue(null != memberId && memberId > 0, "orders is null");

        try {
            List<Long> orderNoList = new ArrayList<>();
            Arrays.asList(orders.split(",")).forEach(e -> orderNoList.add(Long.parseLong(e)));
            Set<Long> orderList = this.shopifyUtils.getFulfillmentByShopifyName(shopifyName, orderNoList, memberId);
            if (CollectionUtil.isNotEmpty(orderList)) {
                this.asyncTask.getShopifyImgByList(orderList, shopifyName, memberId);
            }
            return CommonResult.success(1);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getFulfillmentByShopifyName,shopifyName[{}],error:", shopifyName, e);
            return CommonResult.failed("getFulfillmentByShopifyName,error:" + e.getMessage());
        }
    }


    @PostMapping("/cancelOrderByShopifyName")
    @ApiOperation("根据shopifyName取消订单")
    public CommonResult cancelOrderByShopifyName(String shopifyName, String orderNo, Long memberId) {
        Assert.isTrue(StrUtil.isNotBlank(shopifyName), "shopifyName null");
        Assert.isTrue(StrUtil.isNotBlank(orderNo), "orderNo null");
        try {
            JSONObject map = this.shopifyUtils.cancelOrderByShopifyName(shopifyName, orderNo, memberId);
            if (null != map && map.size() > 0) {
                // 重新获取下状态
                this.shopifyUtils.getSingleOrder(shopifyName, orderNo, memberId);
                return CommonResult.success(map);
            }
            return CommonResult.failed("cancelOrder error");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("cancelOrderByShopifyName, shopifyName[{}],error:", shopifyName, e);
            return CommonResult.failed(e.getMessage());
        }
    }

}
