package com.macro.mall.shopify.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.shopify.util.ShopifyUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.control
 * @date:2019/11/28
 */
@Slf4j
@RestController
@RequestMapping("/shopify")
@Api(tags = "shopify订单调用接口")
public class ShopifyOrderController {


    @Autowired
    private ShopifyUtils shopifyUtils;

    @PostMapping("/getOrdersByShopifyName")
    public CommonResult getOrdersByShopifyName(@RequestParam("shopifyNameList") List<String> shopifyNameList) {
        Assert.isTrue(CollectionUtil.isNotEmpty(shopifyNameList), "shopifyNameList null");
        try {
            int total = this.shopifyUtils.getOrdersByShopifyName(shopifyNameList);
            return CommonResult.success(total);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getOrdersByShopifyName, shopifyNameList[{}],error:", shopifyNameList, e);
            return CommonResult.failed(e.getMessage());
        }
    }
}
