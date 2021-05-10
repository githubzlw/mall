package com.macro.mall.portal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.PmsSkuStockExample;
import com.macro.mall.portal.service.IPmsSkuStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * sku的库存 服务实现类
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-06
 */
@Service
public class PmsSkuStockServiceImpl implements IPmsSkuStockService {

    @Autowired
    private PmsSkuStockMapper pmsSkuStockMapper;

    @Override
    public PmsSkuStock getSingleSkuStock(PmsSkuStock pmsSkuStock) {


        PmsSkuStockExample example = new PmsSkuStockExample();
        example.createCriteria().andProductIdEqualTo(pmsSkuStock.getProductId())
                .andSkuCodeEqualTo(pmsSkuStock.getSkuCode());
        List<PmsSkuStock> pmsSkuStocks = this.pmsSkuStockMapper.selectByExample(example);
        return CollectionUtil.isNotEmpty(pmsSkuStocks) ? pmsSkuStocks.get(0) : null;
    }

    @Override
    public List<PmsSkuStock> getSkuStockByParam(List<Long> productIdList, List<String> skuCodeList) {

        PmsSkuStockExample example = new PmsSkuStockExample();
        example.createCriteria().andProductIdIn(productIdList)
                .andSkuCodeIn(skuCodeList);
        List<PmsSkuStock> pmsSkuStocks = this.pmsSkuStockMapper.selectByExample(example);
        return pmsSkuStocks;
    }
}
