package com.macro.mall.portal.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.entity.*;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.cache.RedisUtil;
import com.macro.mall.portal.config.MicroServiceConfig;
import com.macro.mall.portal.config.ShopifyConfig;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.enums.PayFromEnum;
import com.macro.mall.portal.service.*;
import com.macro.mall.portal.util.BeanCopyUtil;
import com.macro.mall.portal.util.OrderPrefixEnum;
import com.macro.mall.portal.util.OrderUtils;
import com.macro.mall.portal.util.PayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.controller
 * @date:2021-04-30
 */
@RestController
@Api(tags = "XmsShopifyController", description = "客户的Shopify操作")
@RequestMapping("/shopify")
@Slf4j
public class XmsShopifyController {


    @Autowired
    private MicroServiceConfig microServiceConfig;

    private UrlUtil urlUtil = UrlUtil.getInstance();

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UmsMemberService umsMemberService;
    @Autowired
    private IXmsShopifyOrderinfoService shopifyOrderinfoService;

    @Autowired
    private IXmsShopifyCollectionsService xmsShopifyCollectionsService;
    @Autowired
    private IXmsShopifyAuthService xmsShopifyAuthService;
    @Autowired
    private IXmsShopifyCountryService xmsShopifyCountryService;
    @Autowired
    private IXmsShopifyOrderDetailsService xmsShopifyOrderDetailsService;
    @Autowired
    private PmsPortalProductService pmsPortalProductService;
    @Autowired
    private IXmsCustomerSkuStockService xmsCustomerSkuStockService;
    @Autowired
    private IXmsSourcingListService xmsSourcingListService;
    @Autowired
    private IPmsSkuStockService iPmsSkuStockService;
    @Autowired
    private OrderUtils orderUtils;
    @Autowired
    private PayUtil payUtil;
    @Autowired
    private OmsPortalOrderService omsPortalOrderService;
    @Autowired
    private IXmsCustomerProductService xmsCustomerProductService;


    @PostMapping(value = "/authorization")
    @ApiOperation("请求授权接口")
    public CommonResult authorization(@RequestParam("shopName") String shopName) {

        try {
            UmsMember currentMember = this.umsMemberService.getCurrentMember();

            // 数据库判断是否绑定
            UmsMember byId = this.umsMemberService.getById(currentMember.getId());
            if (StrUtil.isNotEmpty(byId.getShopifyName()) && 1 == byId.getShopifyFlag()) {
                return CommonResult.failed("Already bind shop");
            }

            QueryWrapper<XmsShopifyAuth> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyAuth::getShopName, shopName);
            List<XmsShopifyAuth> list = xmsShopifyAuthService.list(queryWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                list.clear();
                return CommonResult.failed("Already bind shop");
            }

            //请求授权
            JSONObject jsonObject = this.urlUtil.callUrlByGet(this.microServiceConfig.getShopifyUrl() + "/authuri?shop=" + shopName);
            CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);

            if (commonResult.getCode() == 200) {
                JSONObject dataJson = JSON.parseObject(commonResult.getData().toString());
                if (dataJson != null) {
                    String clientId = dataJson.getString("id");
                    System.err.println("clientId:" + clientId);
                    String uri = dataJson.getString("uri");
                    redisUtil.hmsetObj(ShopifyConfig.SHOPIFY_KEY + currentMember.getId(), "clientId", clientId, RedisUtil.EXPIRATION_TIME_1_DAY);
                    return CommonResult.success(uri);
                }
            }
            return commonResult;

        } catch (Exception e) {
            log.error("auth", e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation("shopify授权回调")
    @GetMapping(value = "/authCallback")
    public CommonResult authCallback(String code, String hmac, String timestamp, String state, String shop, String host,
                                     HttpServletRequest request) {

        log.info("code:{},hmac:{},timestamp:{},state:{},shop:{},host:{}", code, hmac, timestamp, state, shop, host);
        String redirectUrl = "redirect:/apa/shopifyBindResult.html";

        Map<String, String[]> parameters = request.getParameterMap();
        String data = null;
        SortedSet<String> keys = new TreeSet<String>(parameters.keySet());
        for (String key : keys) {
            if (!key.equals("hmac") && !key.equals("signature")) {
                if (data == null) {
                    data = key + "=" + request.getParameter(key);
                } else {
                    data = data + "&" + key + "=" + request.getParameter(key);
                }
            }
        }
        Map<String, Object> rsMap = new HashMap<>();
        try {

            UmsMember currentMember = this.umsMemberService.getCurrentMember();
            Object clientId = redisUtil.hmgetObj(ShopifyConfig.SHOPIFY_KEY + currentMember.getId(), "clientId");

            if (null == clientId || StringUtils.isBlank(clientId.toString()) || StringUtils.isBlank(shop)) {
                rsMap.put("result", "Please input shop name to authorize");
                redirectUrl = "redirect:/apa/product-shopify.html";
                rsMap.put("redirectUrl", redirectUrl);
                return CommonResult.failed(JSONObject.toJSONString(rsMap));
            }
            shop = shop.replace(ShopifyConfig.SHOPIFY_COM, "");
            SecretKeySpec keySpec = new SecretKeySpec(clientId.toString().getBytes(), ShopifyConfig.HMAC_ALGORITHM);
            Mac mac = Mac.getInstance(ShopifyConfig.HMAC_ALGORITHM);
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            if (Hex.encodeHexString(rawHmac).equals(hmac)) {
                Map<String, String> mapParam = Maps.newHashMap();
                mapParam.put("shop", shop);
                mapParam.put("code", code);
                mapParam.put("userId", String.valueOf(currentMember.getUsername()));
                mapParam.put("userName", currentMember.getUsername());

                JSONObject jsonObject = this.urlUtil.postURL(microServiceConfig.getShopifyUrl() + "/authGetToken", mapParam);
                CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);

                if (commonResult.getCode() == 200) {
                    // 绑定shopify到客户ID
                    this.umsMemberService.updateShopifyInfo(currentMember.getId(), shop, 1);
                    currentMember.setShopifyFlag(1);
                    currentMember.setShopifyName(shop);


                    this.umsMemberService.updateSecurityContext();

                    // 插入shopify的token
                    // ------------------
                    rsMap.put("shopifyName", shop);
                    rsMap.put("shopifyFlag", 1);
                    // ------------------
                    rsMap.put("redirectUrl", redirectUrl);
                    return CommonResult.success(JSONObject.toJSONString(rsMap));
                } else {
                    log.warn("authorization failed");
                    rsMap.put("result", "Failed");
                    return CommonResult.failed(JSONObject.toJSONString(rsMap));
                }
            } else {
                rsMap.put("result", "HMAC IS NOT VERIFIED");
                return CommonResult.failed(JSONObject.toJSONString(rsMap));
            }
        } catch (Exception e) {
            log.error("auth", e);
            rsMap.put("result", "Error");
            return CommonResult.failed(JSONObject.toJSONString(rsMap));
        }
    }


    @ApiOperation("shopify的订单List列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult list(XmsShopifyOrderinfoParam orderinfoParam) {

        Assert.notNull(orderinfoParam, "orderinfoParam null");

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            orderinfoParam.setShopifyName(currentMember.getShopifyName());
            CommonPage<XmsShopifyOrderComb> list = this.shopifyOrderinfoService.list(orderinfoParam);
            return CommonResult.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("list,orderinfoParam[{}],error:", orderinfoParam, e);
            return CommonResult.failed("query failed");
        }
    }


    @ApiOperation("shopify的订单List状态统计")
    @RequestMapping(value = "/listStatusStatistic", method = RequestMethod.GET)
    public CommonResult listStatusStatistic(XmsShopifyOrderinfoParam orderinfoParam) {

        Assert.notNull(orderinfoParam, "orderinfoParam null");

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            orderinfoParam.setShopifyName(currentMember.getShopifyName());
            orderinfoParam.setFinancialStatus(null);
            orderinfoParam.setFulfillmentStatus(null);
            Map<String, Integer> rsMap = new HashMap<>();
            // 获取all
            int allCount = this.shopifyOrderinfoService.queryCount(orderinfoParam);
            // pending
            orderinfoParam.setFinancialStatus("pending");
            int pendingCount = this.shopifyOrderinfoService.queryCount(orderinfoParam);
            // paid fulfilled
            orderinfoParam.setFulfillmentStatus("fulfilled");
            int pendingFulfilledCount = this.shopifyOrderinfoService.queryCount(orderinfoParam);

            rsMap.put("allCount", allCount);
            rsMap.put("pendingCount", pendingCount);
            rsMap.put("pendingFulfilledCount", pendingFulfilledCount);

            return CommonResult.success(rsMap);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("listStatusStatistic,orderinfoParam[{}],error:", orderinfoParam, e);
            return CommonResult.failed("query failed");
        }
    }


    @ApiOperation("shopify的Collection列表")
    @RequestMapping(value = "/collections", method = RequestMethod.GET)
    public CommonResult collections() {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            QueryWrapper<XmsShopifyCollections> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyCollections::getShopName, currentMember.getShopifyName());
            List<XmsShopifyCollections> list = this.xmsShopifyCollectionsService.list(queryWrapper);
            if (CollectionUtil.isEmpty(list)) {
                Map<String, String> param = new HashMap<>();
                param.put("shopifyName", currentMember.getShopifyName());
                //请求数据
                JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/getCollectionByShopifyName", param);
                if (jsonObject.containsKey("code") && 200 == jsonObject.getIntValue("code")) {
                    list = this.xmsShopifyCollectionsService.list(queryWrapper);
                }
            }
            return CommonResult.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("collections,currentMember[{}],error:", currentMember, e);
            return CommonResult.failed("query failed");
        }
    }

    @ApiOperation("shopify的Country列表")
    @RequestMapping(value = "/countryList", method = RequestMethod.GET)
    public CommonResult countryList() {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            QueryWrapper<XmsShopifyCountry> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyCountry::getShopifyName, currentMember.getShopifyName());
            List<XmsShopifyCountry> list = xmsShopifyCountryService.list(queryWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                list.forEach(e -> e.setProvinces(null));
            }
            return CommonResult.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("countryList,currentMember[{}],error:", currentMember, e);
            return CommonResult.failed("query failed");
        }
    }


    @PostMapping(value = "/getShopifyProducts")
    @ApiOperation("获取客户shopify的商品")
    public CommonResult getShopifyProducts() {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            // 数据库判断是否绑定
            UmsMember byId = this.umsMemberService.getById(currentMember.getId());
            if (StrUtil.isEmpty(byId.getShopifyName()) || 0 == byId.getShopifyFlag()) {
                return CommonResult.failed("Please bind the shopify store first");
            }

            Map<String, String> param = new HashMap<>();
            param.put("shopifyName", byId.getShopifyName());
            param.put("memberId", String.valueOf(byId.getId()));
            param.put("userName", byId.getUsername());

            //请求数据
            JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/getProductsByShopifyName", param);
            CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);
            return commonResult;
        } catch (Exception e) {
            log.error("getShopifyProducts,currentMember[{}],error", currentMember, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping(value = "/addProduct")
    @ApiOperation("铺货到shopify商品")
    public CommonResult addToShopifyProducts(@RequestParam Long productId) {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            // 数据库判断是否绑定
            UmsMember byId = this.umsMemberService.getById(currentMember.getId());
            if (StrUtil.isEmpty(byId.getShopifyName()) || 0 == byId.getShopifyFlag()) {
                return CommonResult.failed("Please bind the shopify store first");
            }

            Map<String, String> param = new HashMap<>();
            param.put("shopname", byId.getShopifyName());
            param.put("pid", String.valueOf(productId));
            param.put("published", "");

            //请求数据
            JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl().replace("8086", "8091") + "/addProduct", param);
            CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);
            return commonResult;
        } catch (Exception e) {
            log.error("getShopifyProducts,currentMember[{}],error", currentMember, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping(value = "/getShopifyOrders")
    @ApiOperation("获取客户shopify的订单")
    public CommonResult getShopifyOrders() {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            // 数据库判断是否绑定
            UmsMember byId = this.umsMemberService.getById(currentMember.getId());
            if (StrUtil.isEmpty(byId.getShopifyName()) || 0 == byId.getShopifyFlag()) {
                return CommonResult.failed("Please bind the shopify store first");
            }

            Map<String, String> param = new HashMap<>();

            param.put("shopifyNameList", byId.getShopifyName());


            //请求数据
            JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/getOrdersByShopifyName", param);
            CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);
            return commonResult;
        } catch (Exception e) {
            log.error("getShopifyProducts,currentMember[{}],error", currentMember, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @GetMapping(value = "/getShopifyName")
    @ApiOperation("获取客户shopify的店铺名称")
    public CommonResult getShopifyName() {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            // 数据库判断是否绑定
            UmsMember byId = this.umsMemberService.getById(currentMember.getId());
            return CommonResult.success(byId.getShopifyName());
        } catch (Exception e) {
            log.error("getShopifyName,currentMember[{}],error", currentMember, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping(value = "/clearCustomShopifyInfo")
    @ApiOperation("清空客户绑定的shopify的店铺数据")
    public CommonResult clearCustomShopifyInfo() {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {

            UmsMember byId = this.umsMemberService.getById(currentMember.getId());
            if (StrUtil.isNotBlank(byId.getShopifyName())) {
                QueryWrapper<XmsShopifyAuth> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("shop_name", byId.getShopifyName());
                this.xmsShopifyAuthService.remove(queryWrapper);
                this.umsMemberService.updateShopifyInfo(byId.getId(), "", 0);
            }
            return CommonResult.success(0);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("clearCustomShopifyInfo,memberId[{}],error:", currentMember.getId(), e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping(value = "/getCollections")
    @ApiOperation("获取客户shopify的Collections")
    public CommonResult getCollections() {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            // 数据库判断是否绑定
            UmsMember byId = this.umsMemberService.getById(currentMember.getId());
            if (StrUtil.isEmpty(byId.getShopifyName()) || 0 == byId.getShopifyFlag()) {
                return CommonResult.failed("Please bind the shopify store first");
            }

            Map<String, String> param = new HashMap<>();
            param.put("shopifyName", byId.getShopifyName());

            //请求数据
            JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/getCollectionByShopifyName", param);
            CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);
            return commonResult;
        } catch (Exception e) {
            log.error("getShopifyProducts,currentMember[{}],error", currentMember, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping(value = "/getPurchaseInfoByShopifyOrder")
    @ApiOperation("获取客户shopify的订单对应的库存")
    public CommonResult getPurchaseInfoByShopifyOrder(Long shopifyOrderNoId) {

        Assert.isTrue(null != shopifyOrderNoId && shopifyOrderNoId > 0, "shopifyOrderNo null");
        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {

            UmsMember umsMember = this.umsMemberService.getById(currentMember.getId());
            XmsShopifyOrderinfo byId = this.shopifyOrderinfoService.getById(shopifyOrderNoId);
            if (null == byId) {
                return CommonResult.failed("no this order");
            }

            // 获取shopify的商品详情
            QueryWrapper<XmsShopifyOrderDetails> detailsWrapper = new QueryWrapper<>();
            detailsWrapper.lambda().eq(XmsShopifyOrderDetails::getOrderNo, byId.getOrderNo());
            List<XmsShopifyOrderDetails> detailsList = this.xmsShopifyOrderDetailsService.list(detailsWrapper);

            List<ShopifyPreOrderItem> preOrderItemList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(detailsList)) {
                List<Long> collect = detailsList.stream().map(XmsShopifyOrderDetails::getLineItemId).collect(Collectors.toList());
                // 获取shopify对应的我司商品ID
                List<XmsShopifyPidInfo> pidInfoList = this.shopifyOrderinfoService.queryByShopifyLineItem(umsMember.getShopifyName(), collect);
                Map<Long, PmsProduct> pmsProductMap = new HashMap<>();// 存放商品信息
                Map<Long, List<XmsCustomerSkuStock>> skuStockMap = new HashMap<>();//存放购买库存信息
                //Map<Long, List<XmsCustomerSkuStock>> comStockMap = new HashMap<>();//存放累加购买库存信息
                if (CollectionUtil.isNotEmpty(pidInfoList)) {
                    List<Long> ids = new ArrayList<>();
                    pidInfoList.forEach(e -> ids.add(Long.parseLong(e.getPid())));
                    // 查询商品信息
                    List<PmsProduct> productList = this.pmsPortalProductService.queryByIds(ids);
                    if (CollectionUtil.isNotEmpty(pidInfoList)) {
                        productList.forEach(e -> pmsProductMap.put(e.getId(), e));
                    }

                    // 查询库存信息
                    List<XmsCustomerSkuStock> customerSkuStockList = this.xmsCustomerSkuStockService.queryByUserInfo(currentMember.getUsername(), currentMember.getId());
                    // 过滤库存无效信息
                    customerSkuStockList = customerSkuStockList.stream().filter(e -> null != e.getStatus() && e.getStatus() > 1 && null != e.getStock() && e.getStock() > 0).collect(Collectors.toList());
                    if(CollectionUtil.isNotEmpty(customerSkuStockList)){
                        skuStockMap = customerSkuStockList.stream().collect(Collectors.groupingBy(XmsCustomerSkuStock::getProductId));
                        /*skuStockMap.forEach((k,v)->{
                            XmsCustomerSkuStock tempSkuStock = new XmsCustomerSkuStock();
                            tempSkuStock.set
                        });*/
                    }
                }

                Map<Long, List<XmsCustomerSkuStock>> finalSkuStockMap = skuStockMap;
                detailsList.forEach(e -> {
                    ShopifyPreOrderItem preOrderItem = new ShopifyPreOrderItem();
                    preOrderItem.setOrderNo(e.getOrderNo());
                    preOrderItem.setProductId(e.getProductId());
                    preOrderItem.setLineItemId(e.getLineItemId());
                    preOrderItem.setNeedNumber(e.getQuantity());
                    // 匹配商品信息
                    if (pmsProductMap.containsKey(e.getProductId())) {
                        PmsProduct pmsProduct = pmsProductMap.get(e.getProductId());
                        preOrderItem.setImg(pmsProduct.getPic());
                        preOrderItem.setPrice(pmsProduct.getPriceXj());
                    }else{
                        preOrderItem.setImg("");
                        preOrderItem.setPrice("0");
                    }
                    if(finalSkuStockMap.containsKey(e.getProductId())){
                        preOrderItem.setStockList(finalSkuStockMap.get(e.getProductId()));
                    } else{
                        preOrderItem.setStockList(new ArrayList<>());
                    }
                    // 匹配购买库存信息
                    preOrderItemList.add(preOrderItem);
                });


            }
            return CommonResult.success(preOrderItemList);
        } catch (Exception e) {
            log.error("getPurchaseInfoByShopifyOrder,shopifyOrderNoId[{}],error", shopifyOrderNoId, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping(value = "/purchaseShopifyOrder")
    @ApiOperation("客户的shopify库存不足时进行购买库存操作")
    public CommonResult purchaseShopifyOrder(HttpServletRequest request, PurchaseShopifyOrderParam purchaseShopifyOrderParam) {

        Assert.isTrue(null != purchaseShopifyOrderParam, "purchaseShopifyOrderParam null");
        Assert.isTrue(null != purchaseShopifyOrderParam.getShopifyOrderId() && purchaseShopifyOrderParam.getShopifyOrderId() > 0, "shopifyOrderNo null");
        Assert.isTrue(CollectionUtil.isNotEmpty(purchaseShopifyOrderParam.getSkuCodeAndNumList()), "skuCodeAndNumList null");
        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {

            XmsShopifyOrderinfo byId = this.shopifyOrderinfoService.getById(purchaseShopifyOrderParam.getShopifyOrderId());
            if (null == byId) {
                return CommonResult.failed("no this shopify order");
            }
            // 开始进行PID和skuNo以及数量的组合
            List<Long> productIdList = new ArrayList<>();
            List<String> skuCodeList = new ArrayList<>();
            Map<String, Integer> orderNumMap = new HashMap<>();// 放入下单的数据

            purchaseShopifyOrderParam.getSkuCodeAndNumList().forEach(e -> {
                String[] split = e.split(":");
                long tempPid = Long.parseLong(split[0].trim());
                productIdList.add(tempPid);
                skuCodeList.add(split[1].trim());
                orderNumMap.put(tempPid + "_" + split[0].trim(), Integer.parseInt(split[2].trim()));
            });

            if(CollectionUtil.isNotEmpty(productIdList)) {

                QueryWrapper<XmsSourcingList> sourcingWrapper = new QueryWrapper<>();
                sourcingWrapper.lambda().eq(XmsSourcingList::getMemberId, currentMember.getId()).in(XmsSourcingList::getProductId, productIdList);
                List<XmsSourcingList> sourcingArrList = this.xmsSourcingListService.list(sourcingWrapper);
                Map<Long, XmsSourcingList> sourcingListMap = new HashMap<>();
                if (CollectionUtil.isEmpty(sourcingArrList)) {
                    return CommonResult.failed("No data available");
                }
                sourcingArrList.forEach(e -> sourcingListMap.put(e.getProductId(), e));
                // 组合需要的数据
                List<PmsSkuStock> skuStockList = this.iPmsSkuStockService.getSkuStockByParam(productIdList, skuCodeList);
                if (CollectionUtil.isEmpty(skuStockList)) {
                    return CommonResult.failed("No data available");
                }

                List<PmsSkuStock> pmsSkuStockList = BeanCopyUtil.deepListCopy(skuStockList);// 拷贝数据
                // 过滤未包含的skuCode并且赋值参数给的数量
                pmsSkuStockList.forEach(e -> e.setLockStock(orderNumMap.getOrDefault(e.getProductId() + "_" + e.getSkuCode(), 0)));
                pmsSkuStockList = pmsSkuStockList.stream().filter(e -> null != e.getLockStock() && e.getLockStock() > 0).collect(Collectors.toList());

                // 根据运输方式算运费
                double totalFreight = 0;

                // 生成订单和订单详情信息
                OmsOrder order = new OmsOrder();
                order.setSourceType(0);
                order.setPayType(1);
                String orderNo = this.omsPortalOrderService.generateOrderSn(order);
                OrderPayParam orderPayParam = new OrderPayParam();
                BeanUtil.copyProperties(purchaseShopifyOrderParam, orderPayParam);
                // 生成订单并且计算总价格
                GenerateOrderParam generateOrderParam = GenerateOrderParam.builder().orderNo(orderNo).totalFreight(totalFreight).currentMember(currentMember).pmsSkuStockList(pmsSkuStockList).orderPayParam(orderPayParam).type(0).build();
                GenerateOrderResult orderResult = this.orderUtils.generateOrder(generateOrderParam);

                sourcingListMap.forEach((k, v) -> {
                    // 更新客户库存数据
                    QueryWrapper<XmsCustomerProduct> productQueryWrapper = new QueryWrapper<>();
                    productQueryWrapper.lambda().eq(XmsCustomerProduct::getProductId, k).eq(XmsCustomerProduct::getSourcingId, v.getId());
                    List<XmsCustomerProduct> list = this.xmsCustomerProductService.list(productQueryWrapper);
                    if (CollectionUtil.isNotEmpty(list)) {
                        if (StrUtil.isNotEmpty(list.get(0).getAddress())) {
                            if (!list.get(0).getAddress().contains(orderPayParam.getReceiverCountry())) {
                                list.get(0).setAddress(list.get(0).getAddress() + "," + orderPayParam.getReceiverCountry() + ",");
                            }
                        } else {
                            list.get(0).setAddress(orderPayParam.getReceiverCountry() + ",");
                        }
                        list.get(0).setUpdateTime(new Date());
                        this.xmsCustomerProductService.updateById(list.get(0));
                    }
                });


                skuCodeList.clear();
                productIdList.clear();
                skuStockList.clear();
                pmsSkuStockList.clear();
                orderNumMap.clear();
                sourcingArrList.clear();
                sourcingListMap.clear();

                return this.payUtil.beforePayAndPay(orderResult, currentMember, request, PayFromEnum.SOURCING_ORDER);

            }else{
                return CommonResult.failed("no this shopify order");
            }
        } catch (Exception e) {
            log.error("purchaseShopifyOrder,purchaseShopifyOrderParam[{}],error", purchaseShopifyOrderParam, e);
            return CommonResult.failed(e.getMessage());
        }
    }

}
