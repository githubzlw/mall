package com.macro.mall.shopify.controller;


import cn.hutool.core.util.StrUtil;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.shopify.cache.RedisUtil;
import com.macro.mall.shopify.service.XmsShopifyProductService;
import com.macro.mall.shopify.util.ShopifyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/shopify")
@Api(tags = "shopify商品调用接口")
public class ShopifyProductController {

    @Autowired
    private XmsShopifyProductService shopifyService;

    @Autowired
    private ShopifyUtils shopifyUtils;

    @Autowired
    private RedisUtil redisUtil;


    @PostMapping("/getCollectionByShopifyName")
    public CommonResult getCollectionByShopifyName(String shopifyName) {
        Assert.isTrue(StrUtil.isNotEmpty(shopifyName), "shopifyName null");
        Map<String, String> map = new HashMap<>();
        try {
            Object val = this.redisUtil.hmgetObj(RedisUtil.GET_COLLECTION_BY_SHOPIFY_NAME, shopifyName);
            if (null != val && "success".equalsIgnoreCase(val.toString())) {
                return CommonResult.success("this shop is execute!!");
            }
            map.put(shopifyName, "success");
            this.redisUtil.hmset(RedisUtil.GET_COLLECTION_BY_SHOPIFY_NAME, map, 60);
            int total = this.shopifyUtils.getCollectionByShopifyName(shopifyName);
            return CommonResult.success(total);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getCollectionByShopifyName, shopifyName[{}],error:", shopifyName, e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @PostMapping("/getProductsByShopifyName")
    @ApiOperation("根据shopifyName获取商品数据")
    public CommonResult getProductsByShopifyName(@RequestParam("shopifyName") String shopifyName, @RequestParam("memberId") Long memberId, @RequestParam("userName") String userName) {
        Assert.isTrue(StrUtil.isNotEmpty(shopifyName), "shopifyName null");
        Map<String, String> map = new HashMap<>();
        try {
            Object val = this.redisUtil.hmgetObj(RedisUtil.GET_PRODUCTS_BY_SHOPIFY_NAME, shopifyName + "_" + memberId);
            if (null != val && "success".equalsIgnoreCase(val.toString())) {
                return CommonResult.success("this shop is execute!!");
            }

            int total = this.shopifyUtils.getProductsByShopifyName(shopifyName, memberId, userName);
            map.put(shopifyName + "_" + memberId, "success");
            this.redisUtil.hmset(RedisUtil.GET_PRODUCTS_BY_SHOPIFY_NAME, map, 60);
            return CommonResult.success(total);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getProductsByShopifyName, shopifyName[{}],error:", shopifyName, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation("shopify铺货")
    @RequestMapping(value = "/addProduct", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addProduct(@RequestParam String shopname, @RequestParam String pid, @RequestParam String published, @RequestParam String skuCodes) {

        if (StringUtils.isBlank(shopname)) {
            return CommonResult.failed("SHOPNAME IS NULL");
        }
        if (StringUtils.isBlank(pid)) {
            return CommonResult.failed("PRODUCT IS NULL");
        }
        Map<String, String> map = new HashMap<>();
        Object val = this.redisUtil.hmgetObj(RedisUtil.ADD_PRODUCT, shopname + "_" + pid);
        if (null != val && "success".equalsIgnoreCase(val.toString())) {
            return CommonResult.success("this shop is execute!!");
        }

        CommonResult commonResult = shopifyService.pushProduct(pid, shopname, "1".equalsIgnoreCase(published), skuCodes);
        if (null != commonResult && commonResult.getCode() == 200) {
            map.put(shopname + "_" + pid, "success");
            this.redisUtil.hmset(RedisUtil.ADD_PRODUCT, map, 60);
        }
        return commonResult;
    }

}