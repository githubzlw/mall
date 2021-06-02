package com.macro.mall.portal.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.entity.XmsShopifyCollections;
import com.macro.mall.entity.XmsShopifyOrderinfo;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.cache.RedisUtil;
import com.macro.mall.portal.config.MicroServiceConfig;
import com.macro.mall.portal.config.ShopifyConfig;
import com.macro.mall.portal.domain.XmsShopifyOrderinfoParam;
import com.macro.mall.portal.service.IXmsShopifyCollectionsService;
import com.macro.mall.portal.service.IXmsShopifyOrderinfoService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.controller
 * @date:2021-04-30
 */
@RestController
@Api(tags = "XmsShopifyController", description = "客户的Shopify操作")
@RequestMapping("/shopify")
@Slf4j
public class XmsShopifyController {


    @Autowired
    private MicroServiceConfig microServiceConfig;

    private UrlUtil urlUtil = UrlUtil.getInstance();

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UmsMemberService umsMemberService;
    @Autowired
    private IXmsShopifyOrderinfoService shopifyOrderinfoService;

    private final Map<String, UmsMember> umsMemberMap = new HashMap<>();

    @Autowired
    private IXmsShopifyCollectionsService xmsShopifyCollectionsService;


    @PostMapping(value = "/authorization")
    @ApiOperation("请求授权接口")
    @ResponseBody
    public CommonResult authorization(@RequestParam("shopName") String shopName) {

        try {
            UmsMember currentMember = this.umsMemberService.getCurrentMember();

            // 数据库判断是否绑定
            UmsMember byId = this.umsMemberService.getById(currentMember.getId());
            if (StrUtil.isNotEmpty(byId.getShopifyName()) && 1 == byId.getShopifyFlag()) {
                return CommonResult.failed("Already bind shop");
            }

            if (this.umsMemberMap.containsKey(shopName)) {
                return CommonResult.failed("Already bind shop");
            }

            //请求授权
            JSONObject jsonObject = this.urlUtil.callUrlByGet(this.microServiceConfig.getShopifyUrl() + "/authuri?shop=" + shopName);
            CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);

            if (commonResult.getCode() == 200) {
                JSONObject dataJson = JSON.parseObject(commonResult.getData().toString());
                if (dataJson != null) {
                    String clientId = dataJson.getString("id");
                    System.err.println("clientId:" + clientId);
                    String uri = dataJson.getString("uri");
                    redisUtil.hmsetObj(ShopifyConfig.SHOPIFY_KEY + currentMember.getId(), "clientId", clientId, RedisUtil.EXPIRATION_TIME_1_DAY);
                    this.umsMemberMap.put(shopName, null);
                    return CommonResult.success(uri);
                }
            }
            return commonResult;

        } catch (Exception e) {
            log.error("auth", e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation("shopify授权回调")
    @GetMapping(value = "/authCallback")
    public CommonResult authCallback(String code, String hmac, String timestamp, String state, String shop, String host,
                                     HttpServletRequest request) {

        log.info("code:{},hmac:{},timestamp:{},state:{},shop:{},host:{}", code, hmac, timestamp, state, shop, host);
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

            UmsMember currentMember = this.umsMemberService.getCurrentMember();
            Object clientId = redisUtil.hmgetObj(ShopifyConfig.SHOPIFY_KEY + currentMember.getId(), "clientId");

            if (null == clientId || StringUtils.isBlank(clientId.toString()) || StringUtils.isBlank(shop)) {
                rsMap.put("result", "Please input shop name to authorize");
                redirectUrl = "redirect:/apa/product-shopify.html";
                rsMap.put("redirectUrl", redirectUrl);
                return CommonResult.failed(JSONObject.toJSONString(rsMap));
            }
            shop = shop.replace(ShopifyConfig.SHOPIFY_COM, "");
            SecretKeySpec keySpec = new SecretKeySpec(clientId.toString().getBytes(), ShopifyConfig.HMAC_ALGORITHM);
            Mac mac = Mac.getInstance(ShopifyConfig.HMAC_ALGORITHM);
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            if (Hex.encodeHexString(rawHmac).equals(hmac)) {
                Map<String, String> mapParam = Maps.newHashMap();
                mapParam.put("shop", shop);
                mapParam.put("code", code);
                mapParam.put("userId", String.valueOf(currentMember.getUsername()));
                mapParam.put("userName", currentMember.getUsername());

                JSONObject jsonObject = this.urlUtil.postURL(microServiceConfig.getShopifyUrl() + "/authGetToken", mapParam);
                CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);

                if (commonResult.getCode() == 200) {
                    // 绑定shopify到客户ID
                    this.umsMemberService.updateShopifyInfo(currentMember.getId(), shop, 1);
                    currentMember.setShopifyFlag(1);
                    currentMember.setShopifyName(shop);
                    // 插入shopify的token
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


    @ApiOperation("shopify的订单List列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        XmsShopifyOrderinfoParam orderinfoParam = new XmsShopifyOrderinfoParam();
        try {

            orderinfoParam.setPageNum(pageNum);
            orderinfoParam.setPageSize(pageSize);
            orderinfoParam.setShopifyName(this.umsMemberService.getCurrentMember().getShopifyName());
            Page<XmsShopifyOrderinfo> listPage = this.shopifyOrderinfoService.list(orderinfoParam);
            return CommonResult.success(listPage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("list,orderinfoParam[{}],error:", orderinfoParam, e);
            return CommonResult.failed("query failed");
        }
    }


    @ApiOperation("shopify的Collection列表")
    @RequestMapping(value = "/collections", method = RequestMethod.GET)
    public CommonResult collections() {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            QueryWrapper<XmsShopifyCollections> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyCollections::getShopName, currentMember.getShopifyName());
            List<XmsShopifyCollections> list = this.xmsShopifyCollectionsService.list(queryWrapper);
            return CommonResult.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("collections,currentMember[{}],error:", currentMember, e);
            return CommonResult.failed("query failed");
        }
    }


    @PostMapping(value = "/getShopifyProducts")
    @ApiOperation("获取客户shopify的商品")
    @ResponseBody
    public CommonResult getShopifyProducts() {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            // 数据库判断是否绑定
            UmsMember byId = this.umsMemberService.getById(currentMember.getId());
            if (StrUtil.isEmpty(byId.getShopifyName()) || 0 == byId.getShopifyFlag()) {
                return CommonResult.failed("Please bind the shopify store first");
            }

            Map<String, String> param = new HashMap<>();
            param.put("shopifyName", byId.getShopifyName());
            param.put("memberId", String.valueOf(byId.getId()));
            param.put("userName", byId.getUsername());

            //请求数据
            JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/getProductsByShopifyName", param);
            CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);
            return commonResult;
        } catch (Exception e) {
            log.error("getShopifyProducts,currentMember[{}],error", currentMember, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping(value = "/addProduct")
    @ApiOperation("铺货到shopify商品")
    @ResponseBody
    public CommonResult addToShopifyProducts(@RequestParam Long productId) {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            // 数据库判断是否绑定
            UmsMember byId = this.umsMemberService.getById(currentMember.getId());
            if (StrUtil.isEmpty(byId.getShopifyName()) || 0 == byId.getShopifyFlag()) {
                return CommonResult.failed("Please bind the shopify store first");
            }

            Map<String, String> param = new HashMap<>();
            param.put("shopname", byId.getShopifyName());
            param.put("pid", String.valueOf(productId));
            param.put("published", "");

            //请求数据
            JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl().replace("8086", "8091") + "/addProduct", param);
            CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);
            return commonResult;
        } catch (Exception e) {
            log.error("getShopifyProducts,currentMember[{}],error", currentMember, e);
            return CommonResult.failed(e.getMessage());
        }
    }


}
