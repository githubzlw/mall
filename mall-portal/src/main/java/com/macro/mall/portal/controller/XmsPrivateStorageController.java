package com.macro.mall.portal.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsCustomerSkuStock;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.XmsCustomerProductQuery;
import com.macro.mall.portal.domain.XmsCustomerProductResult;
import com.macro.mall.portal.domain.XmsCustomerProductStockParam;
import com.macro.mall.portal.service.IXmsCustomerSkuStockService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.service.XmsPrivateStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.controller
 * @date:2021-05-07
 */
@Api(tags = "XmsPrivateStorageController", description = "个人库存操作相关接口")
@RestController
@RequestMapping("/privateStorage")
@Slf4j
public class XmsPrivateStorageController {


    @Autowired
    private UmsMemberService umsMemberService;
    @Autowired
    private IXmsCustomerSkuStockService iXmsCustomerSkuStockService;

    @Autowired
    private XmsPrivateStorageService xmsPrivateStorageService;


    @ApiOperation("获取客户产品库存列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult list(XmsCustomerProductStockParam productStockParam) {

        Assert.notNull(productStockParam, "productStockParam null");
        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {

            if (StrUtil.isNotEmpty(productStockParam.getTitle())) {
                productStockParam.setTitle(null);
            }

            if (null == productStockParam.getPageNum() || productStockParam.getPageNum() <= 0) {
                productStockParam.setPageNum(1);
            }

            if (null == productStockParam.getPageSize() || productStockParam.getPageSize() <= 0) {
                productStockParam.setPageSize(10);
            }
            productStockParam.setPageNum((productStockParam.getPageNum() - 1) * productStockParam.getPageSize());
            productStockParam.setMemberId(currentMember.getId());

            Map<String, XmsCustomerProductResult> resultMap = new HashMap<>();
            List<XmsCustomerProductQuery> productRsList = this.xmsPrivateStorageService.queryProductByParam(productStockParam);
            if (CollectionUtil.isNotEmpty(productRsList)) {

                //组合结果

                productRsList.forEach(e -> {
                    if (!resultMap.containsKey(e.getProductId() + "_" + e.getSkuCode())) {
                        XmsCustomerProductResult productResult = new XmsCustomerProductResult();
                        productResult.setProductId(e.getProductId());
                        productResult.setSkuCode(e.getSkuCode());
                        productResult.setImg(e.getImg());
                        productResult.setTitle(e.getTitle());
                        resultMap.put(e.getProductId() + "_" + e.getSkuCode(), productResult);
                    }
                });

                List<Integer> tempProductIds = productRsList.stream().mapToInt(XmsCustomerProductQuery::getProductId).boxed().collect(Collectors.toList());
                List<String> tempSkuCodes = productRsList.stream().map(XmsCustomerProductQuery::getSkuCode).collect(Collectors.toList());

                QueryWrapper<XmsCustomerSkuStock> stockQueryWrapper = new QueryWrapper<>();
                stockQueryWrapper.lambda().in(XmsCustomerSkuStock::getProductId, tempProductIds).in(XmsCustomerSkuStock::getSkuCode, tempSkuCodes);

                List<XmsCustomerSkuStock> list = this.iXmsCustomerSkuStockService.list(stockQueryWrapper);
                if (CollectionUtil.isNotEmpty(list)) {
                    list.forEach(e -> {
                        String tempSek = e.getProductId() + "_" + e.getSkuCode();
                        if (resultMap.containsKey(tempSek)) {
                            XmsCustomerProductResult tempObj = resultMap.get(tempSek);
                            if(StrUtil.isEmpty(tempObj.getSkuData())){
                                tempObj.setSkuData(e.getSpData());
                            }
                            switch (e.getStatus()) {
                                case 0:
                                    tempObj.setPendingArrival(tempObj.getPendingArrival() + 1);
                                case 1:
                                    tempObj.setAvailable(tempObj.getAvailable() + 1);
                                case 2:
                                    tempObj.setReserved(tempObj.getReserved() + 1);
                                case 3:
                                    tempObj.setAwaitingShipment(tempObj.getAwaitingShipment() + 1);
                                case 4:
                                    tempObj.setFulfilled(tempObj.getFulfilled() + 1);
                            }
                        }
                    });
                    list.clear();
                }

                tempProductIds.clear();
                tempSkuCodes.clear();
            }

            return CommonResult.success(resultMap.values());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("list,productStockParam[{}],error:", productStockParam, e);
            return CommonResult.failed("query failed");
        }
    }

}
