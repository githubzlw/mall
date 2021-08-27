package com.macro.mall.shopify.service;

import com.macro.mall.common.api.CommonResult;

/**
 * ShopifyService
 * Created by zlw on 2021/5/10.
 */
public interface XmsShopifyProductService {

    /**u铺货
     * @param pid
     * @param shopName
     * @param published
     * @return
     */
    CommonResult pushProduct(String pid, String shopName, boolean published, String skuCodes);
}
