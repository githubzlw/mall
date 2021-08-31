package com.macro.mall.shopify.service;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.shopify.pojo.AddProductBean;

/**
 * ShopifyService
 * Created by zlw on 2021/5/10.
 */
public interface XmsShopifyProductService {

    /**u铺货
     * @param addProductBean
     * @return
     */
    CommonResult pushProduct(AddProductBean addProductBean);
}
