package com.macro.mall.shopify.controller;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsShopifyAuth;
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


    @PostMapping(value = "/authGetToken")
    @ApiOperation("授权回调")
    public CommonResult authGetToken(
            @ApiParam(name = "code", value = "shopify返回的code", required = true) String code,
            @ApiParam(name = "shop", value = "shopify店铺名", required = true) String shop,
            @ApiParam(name = "userId", value = "客户ID", required = true) String userId,
            @ApiParam(name = "userName", value = "客户名称", required = true) String userName) {

        log.info("code:{},shop:{}", code, shop);
        try {
            HashMap<String, String> result = this.xmsShopifyAuthService.getAccessToken(shop, code);
            String accessToken = result.get("access_token");
            String scope = result.get("scope");

            XmsShopifyAuth shopifyAuth = new XmsShopifyAuth();
            shopifyAuth.setCreateTime(new Date());
            shopifyAuth.setUpdateTime(new Date());
            shopifyAuth.setShopName(shop);
            shopifyAuth.setScope(scope);
            shopifyAuth.setAccessToken(accessToken);
            this.xmsShopifyAuthService.save(shopifyAuth);
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("auth", e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @GetMapping(value = "/authuri")
    @ApiOperation("请求授权接口")
    public CommonResult authUri(String shop) {
        try {
            //请求授权
            String shopUrl = shop;
            if (!StringUtils.startsWithIgnoreCase(shop, "https://")
                    && !StringUtils.startsWithIgnoreCase(shop, "http://")) {
                shopUrl = "https://" + shop + ".myshopify.com";
            }
            if (UrlUtil.getInstance().isAccessURL(shopUrl)) {
                String authUri = shopUrl + "/admin/oauth/authorize?client_id="
                        + shopifyConfig.SHOPIFY_CLIENT_ID + "&scope=" + shopifyConfig.SHOPIFY_SCOPE + "&redirect_uri="
                        + shopifyConfig.SHOPIFY_REDIRECT_URI;

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
