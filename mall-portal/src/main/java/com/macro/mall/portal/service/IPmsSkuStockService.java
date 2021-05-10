package com.macro.mall.portal.service;


import com.macro.mall.model.PmsSkuStock;

import java.util.List;

/**
 * <p>
 * sku的库存 服务类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-06
 */
public interface IPmsSkuStockService {

    /**
     * 获取单个sku的数据
     * @param pmsSkuStock
     * @return
     */
    PmsSkuStock getSingleSkuStock(PmsSkuStock pmsSkuStock);

    /**
     * 获取productIdList和skuCodeList匹配的全部sku数据
     * @param productIdList
     * @param skuCodeList
     * @return
     */
    List<PmsSkuStock> getSkuStockByParam( List<Long> productIdList,List<String> skuCodeList);

}
