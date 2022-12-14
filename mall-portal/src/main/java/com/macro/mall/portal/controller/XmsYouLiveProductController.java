package com.macro.mall.portal.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.BeanCopyUtil;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.entity.XmsCustomerProduct;
import com.macro.mall.entity.XmsCustomerSkuStock;
import com.macro.mall.entity.XmsSourcingList;
import com.macro.mall.model.*;
import com.macro.mall.portal.cache.RedisUtil;
import com.macro.mall.portal.config.MicroServiceConfig;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.enums.OrderPrefixEnum;
import com.macro.mall.portal.enums.PayFromEnum;
import com.macro.mall.portal.service.*;
import com.macro.mall.portal.util.*;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.controller
 * @date:2021-05-07
 */
@Api(tags = "XmsYouLiveProductController", description = "YouLiveProduct操作相关接口")
@RestController
@RequestMapping("/youLiveProduct")
@Slf4j
public class XmsYouLiveProductController {

    @Autowired
    private UmsMemberService umsMemberService;
    @Autowired
    private IXmsCustomerProductService xmsCustomerProductService;
    @Autowired
    private IXmsCustomerSkuStockService xmsCustomerSkuStockService;
    @Autowired
    private OmsCartItemService cartItemService;
    @Autowired
    private IPmsSkuStockService iPmsSkuStockService;
    @Autowired
    private OrderUtils orderUtils;
    @Autowired
    private PayUtil payUtil;
    @Autowired
    private PmsPortalProductService portalProductService;
    @Autowired
    private IXmsSourcingListService xmsSourcingListService;
    @Autowired
    private MicroServiceConfig microServiceConfig;

    private UrlUtil urlUtil = UrlUtil.getInstance();
    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("获取客户产品列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                             @RequestParam(value = "title", defaultValue = "") String title,
                             @RequestParam(value = "shopifyPids", defaultValue = "") String shopifyPids,
                             @RequestParam(value = "importFlag", defaultValue = "-1") Integer importFlag) {

        XmsCustomerProductParam productParam = new XmsCustomerProductParam();
        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {

            productParam.setPageNum(pageNum);
            productParam.setPageSize(pageSize);
            productParam.setTitle(title);
            productParam.setMemberId(currentMember.getId());
            productParam.setUsername(currentMember.getUsername());
            productParam.setShopifyName(currentMember.getShopifyName());
            productParam.setImportFlag(importFlag);
            if (StrUtil.isNotBlank(shopifyPids)) {
                productParam.setShopifyPidList(new ArrayList<>(Arrays.asList(shopifyPids.split(","))));
            }
            Page<XmsCustomerProduct> productPage = this.xmsCustomerProductService.list(productParam);
            if (CollectionUtil.isNotEmpty(productPage.getRecords())) {
                productPage.getRecords().forEach(e -> {
                    e.setShopifyJson(null);
                    if (StrUtil.isNotBlank(currentMember.getShopifyName())) {
                        e.setShopifyProductUrl(String.format(SourcingUtils.SHOPIFY_PRODUCT_URL, currentMember.getShopifyName(), e.getShopifyProductId()));
                    } else {
                        e.setShopifyProductUrl("");
                    }
                    if (StrUtil.isEmpty(e.getAddress())) {
                        e.setAddress("");
                    }
                });
                // 读取产品相关的信息
                List<Long> collect = productPage.getRecords().stream().mapToLong(XmsCustomerProduct::getProductId).boxed().collect(Collectors.toList());
                List<PmsProduct> pmsProducts = this.portalProductService.queryByIds(collect);
                if (CollectionUtil.isNotEmpty(pmsProducts)) {
                    Map<Long, PmsProduct> mapCombos = pmsProducts.stream().collect(Collectors.toMap(PmsProduct::getId, e -> e, (a, b) -> b));
                    productPage.getRecords().forEach(e -> {
                        if (mapCombos.containsKey(e.getProductId())) {
                            e.setTitle(mapCombos.get(e.getProductId()).getName());
                            e.setSourceLink(mapCombos.get(e.getProductId()).getUrl());
                            e.setImg(mapCombos.get(e.getProductId()).getPic());
                            e.setCostPrice(mapCombos.get(e.getProductId()).getPriceXj());
                            if (StrUtil.isEmpty(e.getShopifyPrice())) {
                                e.setShopifyPrice("0");
                            }
                        }
                    });
                }
                // 读取库存相关的信息
                QueryWrapper<XmsCustomerSkuStock> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().in(XmsCustomerSkuStock::getProductId, collect).eq(XmsCustomerSkuStock::getStatus, 2);
                List<XmsCustomerSkuStock> stockList = this.xmsCustomerSkuStockService.list(queryWrapper);
                if (CollectionUtil.isNotEmpty(stockList)) {
                    Map<Long, List<XmsCustomerSkuStock>> listMap = stockList.stream().collect(Collectors.groupingBy(XmsCustomerSkuStock::getProductId));
                    productPage.getRecords().forEach(e -> {
                        e.setStockNum(0);
                        if (listMap.containsKey(e.getProductId())) {
                            int sum = listMap.get(e.getProductId()).stream().mapToInt(XmsCustomerSkuStock::getStock).sum();
                            e.setStockNum(sum);
                        }
                    });
                    listMap.clear();
                }

                collect.clear();

            }
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
        Assert.isTrue(StrUtil.isNotBlank(orderPayParam.getReceiverCountry()), "receiverCountry null");
        Assert.isTrue(StrUtil.isNotBlank(orderPayParam.getReceiverPostCode()), "receiverPostCode null");

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
            String orderNo = this.orderUtils.getOrderNoByRedis(OrderPrefixEnum.PURCHASE_STOCK_ORDER.getCode());
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

            orderResult.setTotalFreight(orderPayParam.getShippingCostValue());

            return this.payUtil.beforePayAndPay(orderResult, currentMember, request, PayFromEnum.PURCHASE_INVENTORY, this.redisUtil);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("youLiveProducts,productParam[{}],error:", productParam, e);
            return CommonResult.failed("query failed");
        }
    }


    @ApiOperation("删除客户产品")
    @RequestMapping(value = "/deleteProduct", method = RequestMethod.POST)
    public CommonResult deleteCustomProduct(@RequestParam("ids") List<Long> ids, @RequestParam("sameShopify") Integer sameShopify) {

        Assert.isTrue(CollectionUtil.isNotEmpty(ids), "ids null");

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        Assert.isTrue(null != currentMember.getId() && currentMember.getId() > 0, "currentMemberId null");
        try {


            /**
             * 删除your live product商品（同时删除shopify上商品时），恢复sourcinglist中商品add to your live product按钮，
             * 只删除your live product商品（未删除shopify上商品时），sourcinglist中商品按钮不恢复
             */
            boolean b;
            List<XmsCustomerProduct> xmsCustomerProducts = this.xmsCustomerProductService.listByIds(ids);
            if (null != sameShopify && sameShopify == 1) {

                StringBuffer sb = new StringBuffer();
                ids.forEach(e -> sb.append(",").append(e));
                // 进行shopify的客户商品删除
                Map<String, String> param = new HashMap<>();
                param.put("idList", sb.substring(1));
                param.put("shopifyName", currentMember.getShopifyName());
                param.put("memberId", String.valueOf(currentMember.getId()));
                JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/deleteProduct", param);
                b = null != jsonObject && jsonObject.containsKey("code") && jsonObject.getIntValue("code") == 200;
                if (b) {

                    return CommonResult.success(true);
                }
            } else {
                //List<Long> productIds = new ArrayList<>();
                /*if (CollectionUtil.isNotEmpty(xmsCustomerProducts)) {
                    List<Long> collect = xmsCustomerProducts.stream().mapToLong(XmsCustomerProduct::getSourcingId).boxed().collect(Collectors.toList());
                    //productIds = xmsCustomerProducts.stream().mapToLong(XmsCustomerProduct::getProductId).boxed().collect(Collectors.toList());
                    UpdateWrapper<XmsSourcingList> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.lambda().in(XmsSourcingList::getId, collect).set(XmsSourcingList::getAddProductFlag, 0);
                    this.xmsSourcingListService.update(null, updateWrapper);
                    collect.clear();
                    xmsCustomerProducts.clear();
                }*/

                if (CollectionUtil.isNotEmpty(ids)) {
                    QueryWrapper<XmsCustomerProduct> productQueryWrapper = new QueryWrapper<>();
                    productQueryWrapper.lambda().in(XmsCustomerProduct::getId, ids).eq(XmsCustomerProduct::getMemberId, currentMember.getId());
                    this.xmsCustomerProductService.remove(productQueryWrapper);
                }
                /*if (CollectionUtil.isNotEmpty(productIds)) {
                    // 移除库存
                    UpdateWrapper<XmsCustomerSkuStock> deleteWrapper = new UpdateWrapper<>();
                    deleteWrapper.lambda().in(XmsCustomerSkuStock::getProductId, productIds).eq(XmsCustomerSkuStock::getMemberId, currentMember.getId());
                    this.xmsCustomerSkuStockService.remove(deleteWrapper);
                }*/

                return CommonResult.success("execute success！");
            }

            return CommonResult.failed("delete error!");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("deleteCustomProduct,ids[{}],error:", ids, e);
            return CommonResult.failed("delete failed");
        }
    }

    @ApiOperation("shopify的商品添加到sourcing中去")
    @RequestMapping(value = "/shopfiyToSourcing", method = RequestMethod.POST)
    public CommonResult shopfiyToSourcing(Long customerProductId) {

        Assert.isTrue(null != customerProductId && customerProductId > 0, "customerProductId null");
        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            XmsCustomerProduct byId = this.xmsCustomerProductService.getById(customerProductId);
            if (null == byId) {
                return CommonResult.failed("no this customerProduct");
            }
            Map<String, String> param = new HashMap<>();
            param.put("customerProductId", String.valueOf(customerProductId));
            //请求数据
            JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getProductUrl() + "/saveShopifyProduct", param);
            if (null == jsonObject || jsonObject.size() == 0) {
                return CommonResult.failed("shopfiyToSourcing failed");
            }
            return JSONObject.parseObject(jsonObject.toJSONString(), CommonResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("shopfiyToSourcing,customerProductId[{}],error:", customerProductId, e);
            return CommonResult.failed("shopfiyToSourcing failed");
        }
    }


}
