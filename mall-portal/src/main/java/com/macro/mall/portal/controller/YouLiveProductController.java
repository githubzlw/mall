package com.macro.mall.portal.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.entity.XmsCustomerProduct;
import com.macro.mall.model.OmsCartItem;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.*;
import com.macro.mall.portal.util.BeanCopyUtil;
import com.macro.mall.portal.util.OrderPrefixEnum;
import com.macro.mall.portal.util.OrderUtils;
import com.macro.mall.portal.util.PayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
@Api(tags = "YouLiveProductController", description = "YouLiveProduct操作相关接口")
@RestController
@RequestMapping("/youLiveProduct")
@Slf4j
public class YouLiveProductController {

    @Autowired
    private UmsMemberService umsMemberService;
    @Autowired
    private IXmsCustomerProductService xmsCustomerProductService;
    @Autowired
    private OmsCartItemService cartItemService;
    @Autowired
    private IPmsSkuStockService iPmsSkuStockService;
    @Autowired
    private OrderUtils orderUtils;
    @Autowired
    private PayUtil payUtil;

    @ApiOperation("获取客户产品列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        XmsCustomerProductParam productParam = new XmsCustomerProductParam();
        try {

            productParam.setPageNum(pageNum);
            productParam.setPageSize(pageSize);
            productParam.setMemberId(this.umsMemberService.getCurrentMember().getId());
            productParam.setUsername(this.umsMemberService.getCurrentMember().getUsername());
            Page<XmsCustomerProduct> productPage = this.xmsCustomerProductService.list(productParam);
            return CommonResult.success(productPage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("youLiveProducts,productParam[{}],error:", productParam, e);
            return CommonResult.failed("query failed");
        }
    }

    @ApiOperation("采购库存")
    @RequestMapping(value = "/purchaseInventory", method = RequestMethod.POST)
    public CommonResult purchaseInventory(HttpServletRequest request, OrderPayParam orderPayParam) {

        Assert.isTrue(null != orderPayParam, "orderPayParam null");
        Assert.isTrue(StrUtil.isNotBlank(orderPayParam.getReceiverName()), "receiverName null");
        Assert.isTrue(StrUtil.isNotBlank(orderPayParam.getReceiverPhone()), "receiverPhone null");
        Assert.isTrue(StrUtil.isNotBlank(orderPayParam.getAddress()), "address null");

        XmsCustomerProductParam productParam = new XmsCustomerProductParam();
        try {

            UmsMember currentMember = this.umsMemberService.getCurrentMember();

            List<OmsCartItem> cartItemList = this.cartItemService.list(currentMember.getId());
            if (CollectionUtil.isNotEmpty(cartItemList)) {
                // 过滤选中的数据
                cartItemList = cartItemList.stream().filter(e -> e.getCheckFlag() == 1).collect(Collectors.toList());
            }

            if (CollectionUtil.isEmpty(cartItemList)) {
                return CommonResult.failed("No shopping cart is selected");
            }
            // 做成map数据
            Map<String, OmsCartItem> cartItemMap = new HashMap<>();
            cartItemList.forEach(e -> cartItemMap.put(e.getProductId() + "_" + e.getProductSkuCode(), e));

            // 查询商品中库存的价格数据
            List<Long> productIdList = cartItemList.stream().mapToLong(OmsCartItem::getProductId).boxed().collect(Collectors.toList());
            List<String> skuCodeList = cartItemList.stream().map(OmsCartItem::getProductSkuCode).collect(Collectors.toList());

            List<PmsSkuStock> skuStockList = this.iPmsSkuStockService.getSkuStockByParam(productIdList, skuCodeList);


            // 拷贝数据
            List<PmsSkuStock> pmsSkuStockList = BeanCopyUtil.deepListCopy(skuStockList);
            // 设置数量和价格
            pmsSkuStockList.forEach(e -> {
                String tempKey = e.getProductId() + "_" + e.getSkuCode();
                if (cartItemMap.containsKey(tempKey)) {
                    e.setStock(cartItemMap.get(tempKey).getQuantity());
                } else {
                    e.setStock(0);
                }
            });

            // 根据运输方式算运费
            double totalFreight = 0;
            String orderNo = this.orderUtils.getOrderNoByRedis(OrderPrefixEnum.LiveProduct.getName());
            // 订单处理
            GenerateOrderParam generateParam = GenerateOrderParam.builder().totalFreight(totalFreight).orderNo(orderNo)
                    .currentMember(currentMember).pmsSkuStockList(pmsSkuStockList).orderPayParam(orderPayParam).type(0).build();
            GenerateOrderResult orderResult = this.orderUtils.generateOrder(generateParam);

            cartItemList.clear();
            cartItemMap.clear();
            productIdList.clear();
            skuCodeList.clear();
            skuStockList.clear();
            pmsSkuStockList.clear();

            return this.payUtil.beforePayAndPay(orderResult, currentMember, request);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("youLiveProducts,productParam[{}],error:", productParam, e);
            return CommonResult.failed("query failed");
        }
    }
}
