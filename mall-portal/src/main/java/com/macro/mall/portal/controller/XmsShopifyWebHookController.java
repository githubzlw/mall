package com.macro.mall.portal.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsShopifyWebhook;
import com.macro.mall.portal.service.IXmsShopifyWebhookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Scope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.controller
 * @date:2021-09-06
 */
@Api(tags = "XmsShopifyWebHookController", description = "shopify的webhook日志接口")
@RestController
@RequestMapping("/shopifyWebhook")
@Slf4j
public class XmsShopifyWebHookController {

    @Autowired
    private IXmsShopifyWebhookService xmsShopifyWebhookService;


    @ApiOperation("customers/data_request日志")
    @RequestMapping(value = "/customers/data_request")
    public CommonResult customersDataRequest(HttpServletRequest request) {

        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> headerMap = new HashMap<>();
        try {
            /**
             * {
             * "shop_id": 954889,
             * "shop_domain": "{shop}.myshopify.com",
             * "orders_requested": [299938, 280263, 220458],
             * "customer": {
             *   "id": 191167,
             *   "email": "john@example.com",
             *   "phone":  "555-625-1199"
             * },
             * "data_request": {
             *   "id": 9999
             * }
             * }
             */
            String shopId = "";
            if (null != parameterMap) {
                String[] shop_ids = parameterMap.get("shop_id");
                if (null != shop_ids) {
                    shopId = Arrays.toString(shop_ids);
                }
            }
            XmsShopifyWebhook webhook = new XmsShopifyWebhook();
            webhook.setType(1);
            webhook.setCreateTime(new Date());
            webhook.setShopId(shopId);
            webhook.setPayload(JSONObject.toJSONString(parameterMap));
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String tempStr = headerNames.nextElement();
                headerMap.put(tempStr, request.getHeader(tempStr));
            }
            webhook.setHeaders(JSONObject.toJSONString(headerMap));
            this.xmsShopifyWebhookService.save(webhook);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("customersDataRequest, param[{}],error:", JSONObject.toJSONString(parameterMap), e);
        }
        return CommonResult.success("customers/data_request");
    }

    @ApiOperation("customers/redact日志")
    @RequestMapping(value = "/customers/redact")
    public CommonResult customersRedact(HttpServletRequest request) {

        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> headerMap = new HashMap<>();
        try {
            /**
             * {
             * "shop_id": 954889,
             * "shop_domain": "{shop}.myshopify.com",
             * "customer": {
             *   "id": 191167,
             *   "email": "john@example.com",
             *   "phone": "555-625-1199"
             * },
             * "orders_to_redact": [299938, 280263, 220458]
             * }
             */
            String shopId = "";
            if (null != parameterMap) {
                String[] shop_ids = parameterMap.get("shop_id");
                if (null != shop_ids) {
                    shopId = Arrays.toString(shop_ids);
                }
            }
            XmsShopifyWebhook webhook = new XmsShopifyWebhook();
            webhook.setType(2);
            webhook.setCreateTime(new Date());
            webhook.setShopId(shopId);
            webhook.setPayload(JSONObject.toJSONString(parameterMap));
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String tempStr = headerNames.nextElement();
                headerMap.put(tempStr, request.getHeader(tempStr));
            }
            webhook.setHeaders(JSONObject.toJSONString(headerMap));
            this.xmsShopifyWebhookService.save(webhook);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("customersRedact, param[{}],error:", JSONObject.toJSONString(parameterMap), e);
        }
        return CommonResult.success("customers/redact");
    }

    @ApiOperation("shop/redact日志")
    @RequestMapping(value = "/shop/redact")
    public CommonResult shopRedact(HttpServletRequest request) {

        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> headerMap = new HashMap<>();
        String header = request.getHeader("X-Shopify-Hmac-SHA256");
        Assert.isTrue(StrUtil.isNotBlank(header), "X-Shopify-Hmac-SHA256 null");
        try {

            /**
             * {
             * "shop_id": 954889,
             * "shop_domain": "{shop}.myshopify.com"
             * }
             */
            String shopId = "";
            if (null != parameterMap) {
                String[] shop_ids = parameterMap.get("shop_id");
                if (null != shop_ids) {
                    shopId = Arrays.toString(shop_ids);
                }
            }
            XmsShopifyWebhook webhook = new XmsShopifyWebhook();
            webhook.setType(3);
            webhook.setCreateTime(new Date());
            webhook.setShopId(shopId);
            webhook.setPayload(JSONObject.toJSONString(parameterMap));
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String tempStr = headerNames.nextElement();
                headerMap.put(tempStr, request.getHeader(tempStr));
            }
            webhook.setHeaders(JSONObject.toJSONString(headerMap));
            this.xmsShopifyWebhookService.save(webhook);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("shopRedact, param[{}],error:", JSONObject.toJSONString(parameterMap), e);
        }
        return CommonResult.success("shop/redact");
    }

}
