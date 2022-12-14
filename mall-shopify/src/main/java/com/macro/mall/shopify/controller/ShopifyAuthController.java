package com.macro.mall.shopify.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsShopifyAuth;
import com.macro.mall.shopify.cache.RedisUtil;
import com.macro.mall.shopify.config.ShopifyConfig;
import com.macro.mall.shopify.service.IXmsShopifyAuthService;
import com.macro.mall.shopify.util.UrlUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jack.luo
 */
@Slf4j
@RestController
@RequestMapping("/shopify")
@Api(tags = "shopify授权调用接口")
public class ShopifyAuthController {
    @Autowired
    private ShopifyConfig shopifyConfig;

    @Autowired
    private IXmsShopifyAuthService xmsShopifyAuthService;

    @Autowired
    private RedisUtil redisUtil;


    @PostMapping(value = "/authGetToken")
    @ApiOperation("授权回调")
    public CommonResult authGetToken(
            @ApiParam(name = "code", value = "shopify返回的code", required = true) String code,
            @ApiParam(name = "shop", value = "shopify店铺名", required = true) String shop,
            @ApiParam(name = "uuid", value = "uuid", required = true) String uuid,
            @ApiParam(name = "userId", value = "客户ID", required = true) String userId) {

        log.info("code:{},shop:{}", code, shop);
        Map<String, String> map = new HashMap<>();
        String key = userId + "_" + shop;
        try {

            Object val = this.redisUtil.hmgetObj(RedisUtil.AUTH_GET_TOKEN, key);
            if (null != val && "success".equalsIgnoreCase(val.toString())) {
                return CommonResult.success("this shop is execute!!");
            }

            HashMap<String, String> result = this.xmsShopifyAuthService.getAccessToken(shop, code);
            String accessToken = result.get("access_token");
            String scope = result.get("scope");

            QueryWrapper<XmsShopifyAuth> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyAuth::getShopName, shop).nested(wrapper -> wrapper.eq(XmsShopifyAuth::getMemberId, 0).or().eq(XmsShopifyAuth::getMemberId, userId));

            List<XmsShopifyAuth> list = this.xmsShopifyAuthService.list(queryWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                // 判断有值的，更新值
                XmsShopifyAuth shopifyAuth = list.get(0);
                shopifyAuth.setUpdateTime(new Date());
                shopifyAuth.setScope(scope);
                shopifyAuth.setAccessToken(accessToken);
                shopifyAuth.setMemberId(Long.parseLong(userId));
                shopifyAuth.setShopJson(JSONObject.toJSONString(result));
                shopifyAuth.setUuid(uuid);
                this.xmsShopifyAuthService.updateById(shopifyAuth);
            } else {
                XmsShopifyAuth shopifyAuth = new XmsShopifyAuth();
                shopifyAuth.setCreateTime(new Date());
                shopifyAuth.setUpdateTime(new Date());
                shopifyAuth.setShopName(shop);
                shopifyAuth.setScope(scope);
                shopifyAuth.setAccessToken(accessToken);
                shopifyAuth.setMemberId(Long.parseLong(userId));
                shopifyAuth.setShopJson(JSONObject.toJSONString(result));
                shopifyAuth.setUuid(uuid);
                this.xmsShopifyAuthService.save(shopifyAuth);
            }

            map.put(key, "success");
            this.redisUtil.hmset(RedisUtil.AUTH_GET_TOKEN, map, RedisUtil.EXPIRATION_TIME_1_HOURS);


            //result.put("shopifyId", String.valueOf(shopifyAuth.getId()));
            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("auth", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @GetMapping(value = "/authuri")
    @ApiOperation("请求授权接口")
    public CommonResult authUri(String shop, String uuid) {
        Map<String, String> map = new HashMap<>();
        try {
            Object val = this.redisUtil.hmgetObj(RedisUtil.AUTH_URI, shop);
            if (null != val && "success".equalsIgnoreCase(val.toString())) {
                return CommonResult.success("this shop is execute!!");
            }

            //请求授权
            String shopUrl = shop;
            if (!StringUtils.startsWithIgnoreCase(shop, "https://")
                    && !StringUtils.startsWithIgnoreCase(shop, "http://")) {
                shopUrl = "https://" + shop + ".myshopify.com";
            }
            if (UrlUtil.getInstance().isAccessURL(shopUrl)) {
                String authUri = shopUrl + "/admin/oauth/authorize?client_id="
                        + shopifyConfig.SHOPIFY_CLIENT_ID + "&scope=" + shopifyConfig.SHOPIFY_SCOPE + "&redirect_uri="
                        + shopifyConfig.SHOPIFY_REDIRECT_URI + "&grant_options[]=per-user&state=" + uuid;
                map.put(shop, "success");
                this.redisUtil.hmset(RedisUtil.AUTH_URI, map, RedisUtil.EXPIRATION_TIME_1_HOURS);
                return CommonResult.success(new Gson().toJson(
                        ImmutableMap.of("id", shopifyConfig.SHOPIFY_CLIENT_SECRET, "uri", authUri)), "AUTH URI");
            } else {
                return CommonResult.failed("The shop name is invalid");
            }


        } catch (Exception e) {
            log.error("auth", e);
            return CommonResult.failed(e.getMessage());
        }
    }
}
