package com.macro.mall.shopify.controller;


import com.macro.mall.common.api.CommonResult;
import com.macro.mall.shopify.service.XmsShopifyProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/shopify")
@Api(tags = "shopify铺货调用接口")
public class ShopifyProductController {

    @Autowired
    private XmsShopifyProductService shopifyService ;

    @ApiOperation("shopify铺货")
    @RequestMapping(value = "/addProduct", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addProduct(@RequestParam String shopname, @RequestParam String pid, @RequestParam String published) {

        if (StringUtils.isBlank(shopname)) {
            return CommonResult.failed("SHOPNAME IS NULL");
        }
        if (StringUtils.isBlank(pid)) {
            return CommonResult.failed("PRODUCT IS NULL");
        }
        return shopifyService.pushProduct(pid, shopname,"1".equalsIgnoreCase(published));
    }

}