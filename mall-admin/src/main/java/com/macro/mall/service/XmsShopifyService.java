package com.macro.mall.service;

import com.macro.mall.common.api.CommonResult;

/**
 * ShopifyService
 * Created by zlw on 2021/5/10.
 */
public interface XmsShopifyService {

    /**按照所选sku铺货
     * @param itemId
     * @param shopName
     * @param skus
     * @param published
     * @return
     */
    CommonResult pushProduct(String itemId, String shopName, String skus, boolean published);
}
