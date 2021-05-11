package com.macro.mall.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.domain.ProductRequestWrap;
import com.macro.mall.domain.ProductWraper;
import com.macro.mall.service.XmsShopifyService;
import com.macro.mall.util.UrlUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * ShopifyService实现类
 * Created by zlw on 2021/5/10.
 */
@Service
public class XmsShopifyServiceImpl implements XmsShopifyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmsShopifyServiceImpl.class);
    private final String PUSH_URL= UrlUtil.ZUUL_SHOPIFY+"shopify/push/product";

@Override
public CommonResult pushProduct(String itemId, String shopName, String skus, boolean published) {
    try {

        LOGGER.info("begin push product[{}] to shopify[{}]",itemId,shopName);
        ProductRequestWrap wrap = new ProductRequestWrap();
        wrap.setPid(itemId);
        wrap.setPublished(published);
//        wrap.setSite(siteEnum.getCode());
        wrap.setShopname(shopName);
        List<String> lstSku = StringUtils.isBlank(skus) ? Lists.newArrayList(): Arrays.asList(skus.split(","));
        wrap.setSkus(lstSku);
        JSONObject json = UrlUtil.getInstance().callUrlByPost(PUSH_URL, wrap);
        CommonResult result = JSON.toJavaObject(json,CommonResult.class);
        if(result != null && result.getCode() == 200){
            ProductWraper wraper = new Gson().fromJson(result.getData().toString(), ProductWraper.class);
            if(wraper != null && wraper.getProduct() != null && wraper.getProduct().getId() != 0L && !wraper.isPush()){
                return CommonResult.success("PUSH SUCCESSED");
            }else if(wraper != null && wraper.isPush()){
                return CommonResult.failed("PRODUCT HAD PUSHED");
            }else{
                return CommonResult.failed("NO PRODUCT TO PUSH");
            }
        }else{
            return CommonResult.failed(result == null ? "REQUEST PUSH PRODUCT HAPPENED ERROR" : result.getMessage());
        }
    } catch (Exception e) {
        LOGGER.error("PUSH PRODUCT FAILED:", e);
        return CommonResult.failed(e.getMessage());
    }
}
}
