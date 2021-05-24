package com.macro.mall.shopify.controller;


import cn.hutool.core.util.StrUtil;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.shopify.util.ShopifyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/shopify")
@Api(tags = "shopify商品调用接口")
public class ShopifyProductController {

    @Autowired
    private ShopifyUtils shopifyUtils;

    @PostMapping("/getProductsByShopifyName")
    @ApiOperation("根据shopifyName获取商品数据")
    public CommonResult getProductsByShopifyName(@RequestParam("shopifyName") String shopifyName, @RequestParam("memberId") Long memberId, @RequestParam("userName") String userName) {
        Assert.isTrue(StrUtil.isNotEmpty(shopifyName), "shopifyName null");
        try {
            int total = this.shopifyUtils.getProductsByShopifyName(shopifyName, memberId, userName);
            return CommonResult.success(total);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getProductsByShopifyName, shopifyName[{}],error:", shopifyName, e);
            return CommonResult.failed(e.getMessage());
        }
    }


}