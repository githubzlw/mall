package com.macro.mall.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsCustomerSkuStock;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.service.IXmsCustomerSkuStockService;
import com.macro.mall.service.OmsOrderService;
import com.macro.mall.service.PmsSkuStockService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.controller
 * @date:2021-07-29
 */
@RestController
@Api(tags = "XmsOrderController", description = "后台订单更新相关接口")
@RequestMapping("/xmsOrder")
@Slf4j
public class XmsOrderController {


    @Autowired
    private OmsOrderService orderService;
    @Autowired
    private IXmsCustomerSkuStockService iXmsCustomerSkuStockService;

    @Autowired
    private PmsSkuStockService pmsSkuStockService;


    @PostMapping("/updateSourcingOrder")
    public CommonResult updateSourcingOrder(String orderNo, Integer state) {
        Assert.isTrue(StrUtil.isNotBlank(orderNo), "orderNo null");
        Assert.isTrue(null != state && state > -1, "state null");

        try {

            int i = this.orderService.updateOrderStatus(orderNo, state);
            return CommonResult.success(i);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("updateSourcingOrder,orderNo[{}],state[{}],error:", orderNo, state, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping("/updateOrderStock")
    public CommonResult updateOrderStock(String orderNo, Integer state) {
        Assert.isTrue(StrUtil.isNotBlank(orderNo), "orderNo null");
        Assert.isTrue(null != state && state > -1, "state null");

        try {

            int i = this.iXmsCustomerSkuStockService.updateStateByOrderNo(orderNo, state);
            return CommonResult.success(i);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("updateSourcingOrder,orderNo[{}],state[{}],error:", orderNo, state, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @GetMapping("/getOrderStockList/{orderNo}")
    public CommonResult getOrderStockList(@PathVariable("orderNo") String orderNo) {
        try {

            QueryWrapper<XmsCustomerSkuStock> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsCustomerSkuStock::getOrderNo, orderNo);
            List<XmsCustomerSkuStock> list = this.iXmsCustomerSkuStockService.list(queryWrapper);

            if (CollectionUtil.isNotEmpty(list)) {
                Map<String, XmsCustomerSkuStock> rsMap = new HashMap<>();
                list.forEach(e -> rsMap.put(e.getProductId() + "_" + e.getSkuCode(), e));
                List<Long> collect = list.stream().mapToLong(XmsCustomerSkuStock::getProductId).boxed().collect(Collectors.toList());
                List<PmsSkuStock> listByProductIds = pmsSkuStockService.getListByProductIds(collect);
                if (CollectionUtil.isNotEmpty(listByProductIds)) {
                    listByProductIds.forEach(e -> {
                        if (rsMap.containsKey(e.getProductId() + "_" + e.getSkuCode())) {
                            rsMap.get(e.getProductId() + "_" + e.getSkuCode()).setPic(e.getPic());
                        }
                    });
                    listByProductIds.clear();
                }
                rsMap.clear();
            }
            return CommonResult.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getOrderStockList,orderNo[{}],error:", orderNo, e);
            return CommonResult.failed(e.getMessage());
        }
    }


}
