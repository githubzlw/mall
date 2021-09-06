package com.macro.mall.portal.controller;

import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsShopifyWebhook;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

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


    @ApiOperation("customers/data_request日志")
    @RequestMapping(value = "/customers/data_request")
    public CommonResult customersDataRequest(HttpServletRequest request) {

        Map<String, String[]> parameterMap = request.getParameterMap();
        try {
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
        try {
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
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            log.error("shopRedact, param[{}],error:", JSONObject.toJSONString(parameterMap), e);
        }
        return CommonResult.success("shop/redact");
    }

}
