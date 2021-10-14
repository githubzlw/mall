package com.macro.mall.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.config.UrlConfig;
import com.macro.mall.domain.FulfillmentParam;
import com.macro.mall.domain.XmsShopifyOrderinfoParam;
import com.macro.mall.entity.XmsShopifyFulfillment;
import com.macro.mall.entity.XmsShopifyOrderComb;
import com.macro.mall.service.IXmsShopifyFulfillmentService;
import com.macro.mall.service.IXmsShopifyOrderinfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.controller
 * @date:2021-09-13
 */
@RestController
@Api(tags = "XmsShopifyOrderController", description = "shopify数据接口")
@RequestMapping("/xmsShopify")
@Slf4j
public class XmsShopifyOrderController {

    @Autowired
    private IXmsShopifyOrderinfoService shopifyOrderinfoService;

    @Autowired
    private IXmsShopifyFulfillmentService xmsShopifyFulfillmentService;
    @Autowired
    private UrlConfig urlConfig;
    private UrlUtil urlUtil = UrlUtil.getInstance();

    @ApiOperation("shopify的订单List列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult list(XmsShopifyOrderinfoParam orderinfoParam) {

        Assert.notNull(orderinfoParam, "orderinfoParam null");

        try {
            CommonPage<XmsShopifyOrderComb> list = this.shopifyOrderinfoService.list(orderinfoParam);
            return CommonResult.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("list,orderinfoParam[{}],error:", orderinfoParam, e);
            return CommonResult.failed("query failed");
        }
    }


    @ApiOperation("shopify的物流信息")
    @RequestMapping(value = "/logisticsInformation", method = RequestMethod.POST)
    public CommonResult logisticsInformation(Long orderNo, String shopifyName, Long memberId) {

        try {
            QueryWrapper<XmsShopifyFulfillment> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyFulfillment::getOrderId, orderNo).isNotNull(XmsShopifyFulfillment::getTrackingNumber);
            List<XmsShopifyFulfillment> list = this.xmsShopifyFulfillmentService.list(queryWrapper);
            if (CollectionUtil.isEmpty(list)) {
                //请求数据
                Map<String, String> param = new HashMap<>();

                param.put("shopifyName", shopifyName);
                param.put("orders", String.valueOf(orderNo));
                param.put("memberId", String.valueOf(memberId));

                JSONObject jsonObject = this.urlUtil.postURL(this.urlConfig.getShopifyApiUrl() + "/getFulfillmentByShopifyName", param);
                CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);
                if (null != commonResult && commonResult.getCode() == 200) {
                    list = this.xmsShopifyFulfillmentService.list(queryWrapper);
                }
            }
            return CommonResult.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("logisticsInformation,orderNo[{}],error:", orderNo, e);
            return CommonResult.failed("query failed");
        }
    }

    @PostMapping("/createFulfillment")
    @ApiOperation("创建履行订单")
    public CommonResult createFulfillment(FulfillmentParam fulfillmentParam) {

        Assert.notNull(fulfillmentParam, "fulfillmentParam is null");
        Assert.isTrue(StrUtil.isNotBlank(fulfillmentParam.getShopifyName()), "shopifyName is null");
        Assert.isTrue(null != fulfillmentParam.getOrderNo() && fulfillmentParam.getOrderNo() > 0, "orderNo is null");
        Assert.isTrue(StrUtil.isNotBlank(fulfillmentParam.getTrackingNumber()), "trackingNumber is null");
        // Assert.isTrue(StrUtil.isNotBlank(fulfillmentParam.getTrackingCompany()), "trackingCompany is null");


        try {
            Map<String, String> param = new HashMap<>();

            param.put("shopifyName", fulfillmentParam.getShopifyName());
            param.put("orderNo", String.valueOf(fulfillmentParam.getOrderNo()));
            param.put("trackingNumber", fulfillmentParam.getTrackingNumber());
            param.put("trackingCompany", fulfillmentParam.getTrackingCompany());
            param.put("memberId", String.valueOf(fulfillmentParam.getMemberId()));
            param.put("notifyCustomer", String.valueOf(fulfillmentParam.isNotifyCustomer()));
            param.put("locationId", fulfillmentParam.getLocationId());
            param.put("message", fulfillmentParam.getMessage());

            JSONObject jsonObject = this.urlUtil.postURL(this.urlConfig.getShopifyApiUrl() + "/createFulfillment", param);
            CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);
            if(null != commonResult && commonResult.getCode() == 200){
                // 执行成功后，再查询一次
                Map<String, String> rmParam = new HashMap<>();

                rmParam.put("shopifyName", fulfillmentParam.getShopifyName());
                rmParam.put("orders", String.valueOf(fulfillmentParam.getOrderNo()));
                rmParam.put("memberId", String.valueOf(fulfillmentParam.getMemberId()));
                this.urlUtil.postURL(this.urlConfig.getShopifyApiUrl() + "/getFulfillmentByShopifyName", rmParam);
            }
            return commonResult;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("createFulfillment fulfillmentParam:[{}],error:", fulfillmentParam, e);
            return CommonResult.failed("fulfillmentParam:[{" + JSONObject.toJSONString(fulfillmentParam) + "}]" + ",error:" + e.getMessage());
        }
    }
}
