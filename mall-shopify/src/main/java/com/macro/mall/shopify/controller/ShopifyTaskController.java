package com.macro.mall.shopify.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.shopify.pojo.ShopifyTaskBean;
import com.macro.mall.shopify.util.ShopifyUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.shopify.controller
 * @date:2021-10-15
 */
@Slf4j
@RestController
@RequestMapping("/shopifyTask")
@Api(tags = "shopify定时任务接口")
public class ShopifyTaskController {

    @Resource
    private ShopifyUtils shopifyUtils;

    @PostMapping("/syncInfoByShopifyName")
    public CommonResult syncInfoByShopifyName(String listParam) {


        int size = 0;
        List<ShopifyTaskBean> list = null;
        try {
            list = JSONArray.parseArray(listParam, ShopifyTaskBean.class);
            if (CollectionUtil.isNotEmpty(list)) {
                size = list.size();
                this.shopifyUtils.getAllShopifyInfo(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("syncInfoByShopifyName,list[{}],error:", list, e);
        }
        return CommonResult.success(size);
    }
}
