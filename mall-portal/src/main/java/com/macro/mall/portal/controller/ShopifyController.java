package com.macro.mall.portal.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.cache.RedisUtil;
import com.macro.mall.portal.config.MicroServiceConfig;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.util.UrlUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.controller
 * @date:2021-04-30
 */
@RestController
@Api(tags = "ShopifyController", description = "客户的Shopify操作")
@RequestMapping("/shopify")
@Slf4j
public class ShopifyController {


    @Autowired
    private MicroServiceConfig microServiceConfig;

    @Autowired
    private RedisUtil redisUtil;

    private UrlUtil instance = UrlUtil.getInstance();

    private final Map<String, UmsMember> umsMemberMap = new HashMap<>();


    private static final String SHOPIFY_COM = ".myshopify.com";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final String SHOPIFY_KEY = "sourcing:shopify:";
    private final String CALLBACK_URL = "http://guu8pd.natappfree.cc/shopify/auth/callback";
    private static String clientId = "";


    @PostMapping(value = "/authorization")
    @ApiOperation("请求授权接口")
    @ResponseBody
    public CommonResult authorization(@RequestParam("shopName") String shopName) {
        String userId = "15937";

        try {
            if (umsMemberMap.containsKey(shopName)) {
                return CommonResult.failed("Already bind shop");
            }
            //请求授权
            JSONObject jsonObject = instance.callUrlByGet(microServiceConfig.getUrl() + UrlUtil.MICRO_SERVICE_SHOPIFY
                    + "shopify/authuriAndCallBack?shop=" + shopName + "&callBackUrl=" + CALLBACK_URL);
            CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);
            if (commonResult.getCode() == 200) {
                JSONObject dataJson = JSON.parseObject(commonResult.getData().toString());
                if (dataJson != null) {
                    clientId = dataJson.getString("id");
                    System.err.println("clientId:" + clientId);
                    String uri = dataJson.getString("uri");
                    redisUtil.hmsetObj(SHOPIFY_KEY + userId, "shopName", shopName);
                    redisUtil.hmsetObj(SHOPIFY_KEY + userId, "uri", uri);
                    redisUtil.hmsetObj(SHOPIFY_KEY + shopName, "clientId", clientId);
                    umsMemberMap.put(shopName, null);
                    return CommonResult.success(uri);
                }
            }
            return commonResult;

        } catch (Exception e) {
            log.error("auth", e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @RequestMapping(value = "/auth/callback")
    public CommonResult authCallback(String code, String hmac, String timestamp, String state, String shop,
                             HttpServletRequest request) {

        log.info("code:{},hmac:{},timestamp:{},state:{},shop:{}", code, hmac, timestamp, state, shop);
        String redirectUrl = "redirect:/apa/shopifyBindResult.html";

        Map<String, String[]> parameters = request.getParameterMap();
        String data = null;
        SortedSet<String> keys = new TreeSet<String>(parameters.keySet());
        for (String key : keys) {
            if (!key.equals("hmac") && !key.equals("signature")) {
                if (data == null) {
                    data = key + "=" + request.getParameter(key);
                } else {
                    data = data + "&" + key + "=" + request.getParameter(key);
                }
            }
        }
        Map<String, Object> rsMap = new HashMap<>();
        try {

            String userId = "15937";

            if (null == clientId || StringUtils.isBlank(clientId) || StringUtils.isBlank(shop)) {
                rsMap.put("result", "Please input shop name to authorize");
                redirectUrl = "redirect:/apa/product-shopify.html";
                rsMap.put("redirectUrl", redirectUrl);
                return CommonResult.failed(JSONObject.toJSONString(rsMap));
            }
            shop = shop.replace(SHOPIFY_COM, "");
            SecretKeySpec keySpec = new SecretKeySpec(clientId.getBytes(), HMAC_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            if (Hex.encodeHexString(rawHmac).equals(request.getParameter("hmac"))) {
                Map<String, String> mapParam = Maps.newHashMap();
                mapParam.put("shop", shop);
                mapParam.put("code", code);
                // mapParam.put("userid", String.valueOf(umsMemberMap.get(shop).getId()));
                mapParam.put("userid", userId);
                JSONObject jsonObject = instance.postURL(microServiceConfig.getUrl() + UrlUtil.MICRO_SERVICE_SHOPIFY + "shopify/auth", mapParam);
                CommonResult result = JSON.toJavaObject(jsonObject, CommonResult.class);
                if (result.getCode() == 200) {
                    // 绑定shopify到客户ID
                    // ------------------
                    rsMap.put("shopifyName", shop);
                    rsMap.put("shopifyFlag", 1);
                    // ------------------
                    rsMap.put("redirectUrl", redirectUrl);
                    return CommonResult.success(JSONObject.toJSONString(rsMap));
                } else {
                    log.warn("authorization failed");
                    rsMap.put("result", "Failed");
                    return CommonResult.failed(JSONObject.toJSONString(rsMap));
                }
            } else {
                rsMap.put("result", "HMAC IS NOT VERIFIED");
                return CommonResult.failed(JSONObject.toJSONString(rsMap));
            }
        } catch (Exception e) {
            log.error("auth", e);
            rsMap.put("result", "Error");
            return CommonResult.failed(JSONObject.toJSONString(rsMap));
        }

    }

}
