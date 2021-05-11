package com.macro.mall.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.XmsSourcingInfoParam;
import com.macro.mall.entity.XmsSourcingList;
import com.macro.mall.service.IXmsSourcingListService;
import com.macro.mall.service.XmsShopifyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;


/**
 * shopify
 */
@RestController
@Api(tags = "XmsShopifyController", description = "shopify")
@RequestMapping("/shopify")
@Slf4j
public class XmsShopifyController {


    @Autowired
    private XmsShopifyService shopifyService ;

    @ApiOperation("shopify铺货")
    @RequestMapping(value = "/addProduct", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addProduct(@RequestParam String itemId,@RequestParam String published) {

//        SiteEnum siteEnum = MultiSiteUtil.getSiteEnumByHost(request.getServerName());
//        UserBean userBean = LoginHelp.getUserBean(request);
//        if(userBean == null){
//            return CommonResult.failed("NOT LOGIN");
//        }
        String shopname ="akersjiang";
        String skus ="color,red";

        if (StringUtils.isBlank(shopname)) {
            return CommonResult.failed("SHOPNAME IS NULL");
        }
        if (StringUtils.isBlank(itemId)) {
            return CommonResult.failed("PRODUCT IS NULL");
        }
        return shopifyService.pushProduct(itemId, shopname,skus,"1".equalsIgnoreCase(published));
    }

}
