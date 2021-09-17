package com.macro.mall.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.config.UrlConfig;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
    public CommonResult logisticsInformation(Long shopifyOrderNo, String shopifyName) {

        try {
            QueryWrapper<XmsShopifyFulfillment> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyFulfillment::getOrderId, shopifyOrderNo);
            XmsShopifyFulfillment one = this.xmsShopifyFulfillmentService.getOne(queryWrapper);
            if (null == one) {
                //请求数据
                Map<String, String> param = new HashMap<>();

                List<Long> orderNoList = new ArrayList<>();
                orderNoList.add(shopifyOrderNo);
                param.put("shopifyName", shopifyName);
                param.put("orderNoList", JSONArray.toJSONString(orderNoList));

                JSONObject jsonObject = this.urlUtil.postURL(this.urlConfig.getShopifyApiUrl() + "/getFulfillmentByShopifyName", param);
                CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);
                if (null != commonResult && commonResult.getCode() == 200) {
                    one = this.xmsShopifyFulfillmentService.getOne(queryWrapper);
                }
            }
            return CommonResult.success(one);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("logisticsInformation,shopifyOrderNo[{}],error:", shopifyOrderNo, e);
            return CommonResult.failed("query failed");
        }
    }
}
