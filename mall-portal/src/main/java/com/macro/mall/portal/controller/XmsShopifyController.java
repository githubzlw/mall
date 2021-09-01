package com.macro.mall.portal.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Maps;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.entity.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.cache.RedisUtil;
import com.macro.mall.portal.config.MicroServiceConfig;
import com.macro.mall.portal.config.ShopifyConfig;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.*;
import com.macro.mall.portal.util.OrderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
import java.util.concurrent.atomic.AtomicInteger;
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
    private OmsPortalOrderService omsPortalOrderService;
    @Autowired
    private OmsCartItemService cartItemService;
    @Autowired
    private IXmsCustomStockLogService xmsCustomStockLogService;
    @Autowired
    private IXmsShopifyProductTypeService xmsShopifyProductTypeService;
    @Autowired
    private IXmsShopifyProductTagService xmsShopifyProductTagService;
    @Autowired
    private IXmsShopifyOrderAddressService xmsShopifyOrderAddressService;


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
    public CommonResult addToShopifyProducts(@RequestParam Long productId, @RequestParam String skuCodes, @RequestParam String collectionId, @RequestParam String productType, @RequestParam String productTags) {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            // 数据库判断是否绑定
            UmsMember byId = this.umsMemberService.getById(currentMember.getId());
            if (StrUtil.isEmpty(byId.getShopifyName()) || 0 == byId.getShopifyFlag()) {
                return CommonResult.failed("Please bind the shopify store first");
            }

            Map<String, String> param = new HashMap<>();
            param.put("shopName", byId.getShopifyName());
            param.put("pid", String.valueOf(productId));
            param.put("skuCodes", skuCodes);
            param.put("published", "0");
            param.put("collectionId", collectionId);
            param.put("productType", productType);
            param.put("productTags", productTags);

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
            Map<String, String> param = new HashMap<>();
            param.put("shopifyName", currentMember.getShopifyName());

            //请求数据
            JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/getCollectionByShopifyName", param);

            if (jsonObject.containsKey("code") && 200 == jsonObject.getIntValue("code")) {
                QueryWrapper<XmsShopifyCollections> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(XmsShopifyCollections::getShopName, currentMember.getShopifyName());
                List<XmsShopifyCollections> list = this.xmsShopifyCollectionsService.list(queryWrapper);
                return CommonResult.success(list);
            } else {
                return JSON.toJavaObject(jsonObject, CommonResult.class);
            }
        } catch (Exception e) {
            log.error("getCollections,currentMember[{}],error", currentMember, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping(value = "/getPurchaseInfoByShopifyOrder")
    @ApiOperation("获取客户shopify的订单对应的库存")
    @ApiImplicitParams({@ApiImplicitParam(name = "shopifyOrderNoId",value = "shopify的订单ID",required = true),
    @ApiImplicitParam(name = "transportType",value = "运输模式: 0 CHINA, 1 USA",required = true)})
    public CommonResult getPurchaseInfoByShopifyOrder(Long shopifyOrderNoId, Integer transportType) {

        Assert.isTrue(null != shopifyOrderNoId && shopifyOrderNoId > 0, "shopifyOrderNo null");
        Assert.isTrue(null != transportType && transportType >= 0 && transportType < 2, "transportType null");
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
                List<Long> collect = detailsList.stream().map(XmsShopifyOrderDetails::getProductId).collect(Collectors.toList());
                // 获取shopify对应的我司商品ID
                List<XmsShopifyPidInfo> pidInfoList = this.shopifyOrderinfoService.queryByShopifyLineItem(umsMember.getShopifyName(), collect);
                collect.clear();
                Map<Long, PmsProduct> pmsProductMap = new HashMap<>();// 存放商品信息
                Map<Long, List<XmsCustomerSkuStock>> skuStockMap = new HashMap<>();//存放购买库存信息
                //Map<Long, List<XmsCustomerSkuStock>> comStockMap = new HashMap<>();//存放累加购买库存信息
                Map<Long, Long> shopifyPidMap = new HashMap<>();
                if (CollectionUtil.isNotEmpty(pidInfoList)) {
                    List<Long> ids = new ArrayList<>();
                    pidInfoList.forEach(e -> {
                        ids.add(Long.parseLong(e.getPid()));
                        shopifyPidMap.put(Long.parseLong(e.getShopifyPid()),Long.parseLong(e.getPid()));
                    });
                    // 查询商品信息
                    List<PmsProduct> productList = this.pmsPortalProductService.queryByIds(ids);
                    if (CollectionUtil.isNotEmpty(pidInfoList)) {
                        productList.forEach(e -> pmsProductMap.put(e.getId(), e));
                    }

                    // 查询库存信息
                    List<XmsCustomerSkuStock> customerSkuStockList = this.xmsCustomerSkuStockService.queryByUserInfo(currentMember.getUsername(), currentMember.getId());
                    // 过滤库存无效信息
                    customerSkuStockList = customerSkuStockList.stream().filter(e -> null != e.getStatus() && e.getStatus() > 1 && null != e.getStock() && e.getStock() > 0 && transportType.equals(e.getShippingFrom())).collect(Collectors.toList());
                    if(CollectionUtil.isNotEmpty(customerSkuStockList)){
                        skuStockMap = customerSkuStockList.stream().collect(Collectors.groupingBy(XmsCustomerSkuStock::getProductId));
                        /*skuStockMap.forEach((k,v)->{
                            XmsCustomerSkuStock tempSkuStock = new XmsCustomerSkuStock();
                            tempSkuStock.set
                        });*/
                        customerSkuStockList.clear();
                    }
                }

                Map<Long, List<XmsCustomerSkuStock>> finalSkuStockMap = skuStockMap;
                detailsList.forEach(e -> {
                    ShopifyPreOrderItem preOrderItem = new ShopifyPreOrderItem();
                    preOrderItem.setOrderNo(e.getOrderNo());
                    //preOrderItem.setProductId(e.getProductId());
                    preOrderItem.setLineItemId(e.getProductId());
                    preOrderItem.setNeedNumber(e.getQuantity());
                    // 匹配商品信息
                    if (shopifyPidMap.containsKey(e.getProductId()) && pmsProductMap.containsKey(shopifyPidMap.get(e.getProductId()))) {
                        PmsProduct pmsProduct = pmsProductMap.get(shopifyPidMap.get(e.getProductId()));
                        preOrderItem.setImg(pmsProduct.getPic());
                        //preOrderItem.setPrice(pmsProduct.getPriceXj());
                        preOrderItem.setProductId(pmsProduct.getId());
                        preOrderItem.setFreeStatus(pmsProduct.getFreeStatus());

                    }else{
                        preOrderItem.setImg("");
                        preOrderItem.setPrice("0");
                        preOrderItem.setProductId(0L);
                        preOrderItem.setFreeStatus(0);
                        preOrderItem.setWeight(0D);
                        preOrderItem.setVolume(0D);
                    }
                    if(shopifyPidMap.containsKey(e.getProductId()) && finalSkuStockMap.containsKey(shopifyPidMap.get(e.getProductId()))){
                        preOrderItem.setStockList(finalSkuStockMap.get(shopifyPidMap.get(e.getProductId())));
                    } else{
                        preOrderItem.setStockList(new ArrayList<>());
                    }
                    // 匹配购买库存信息
                    preOrderItemList.add(preOrderItem);
                });

                detailsList.clear();
                pmsProductMap.clear();
                skuStockMap.clear();
            }
            return CommonResult.success(preOrderItemList);
        } catch (Exception e) {
            log.error("getPurchaseInfoByShopifyOrder,shopifyOrderNoId[{}],error", shopifyOrderNoId, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping(value = "/purchaseShopifyOrderToCart")
    @ApiOperation("客户的shopify库存不足时进行购买库存操作")
    public CommonResult purchaseShopifyOrderToCart(PurchaseShopifyOrderParam purchaseShopifyOrderParam) {

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
                orderNumMap.put(tempPid + "_" + split[1].trim(), Integer.parseInt(split[2].trim()));
            });

            if(CollectionUtil.isNotEmpty(productIdList)) {

                QueryWrapper<XmsSourcingList> sourcingWrapper = new QueryWrapper<>();
                sourcingWrapper.lambda().eq(XmsSourcingList::getMemberId, currentMember.getId()).in(XmsSourcingList::getProductId, productIdList);
                List<XmsSourcingList> sourcingArrList = this.xmsSourcingListService.list(sourcingWrapper);
                if (CollectionUtil.isEmpty(sourcingArrList)) {
                    return CommonResult.failed("No data available");
                }
                // 组合需要的数据
                List<PmsSkuStock> skuStockList = this.iPmsSkuStockService.getSkuStockByParam(productIdList, skuCodeList);
                if (CollectionUtil.isEmpty(skuStockList)) {
                    return CommonResult.failed("No data available");
                }

                List<PmsProduct> productList = this.pmsPortalProductService.queryByIds(productIdList);
                Map<Long, PmsProduct> pmsProductMap = new HashMap<>();
                productList.forEach(e-> pmsProductMap.put(e.getId(), e) );

                List<PmsSkuStock> pmsSkuStockList = new ArrayList<>();
                // 过滤未包含的skuCode并且赋值参数给的数量
                AtomicInteger total = new AtomicInteger();
                skuStockList.forEach(e -> {
                    if(!orderNumMap.containsKey(e.getProductId() + "_" + e.getSkuCode())){
                        total.getAndIncrement();
                    } else{
                        e.setStock(orderNumMap.get(e.getProductId() + "_" + e.getSkuCode()));
                        pmsSkuStockList.add(e);
                    }
                });
                // 如果大于0,说明存在没有匹配上的数据
                if(total.get() > 0){
                    return CommonResult.failed("Parameter data does not match");
                }
                if(CollectionUtil.isEmpty(pmsSkuStockList)){
                    return CommonResult.failed("Insufficient inventory");
                }

                // 开始添加到购物车

                total.set(0);
                pmsSkuStockList.forEach(e->{
                    PmsProduct pmsProduct = pmsProductMap.get(e.getProductId());
                    OmsCartItem cartItem = new OmsCartItem();
                    if(e.getStock() >= e.getMaxMoq()){
                        cartItem.setPrice(e.getMaxPrice());
                    } else if(e.getStock() >= e.getMinMoq()){
                        cartItem.setPrice(e.getMinPrice());
                    } else{
                        cartItem.setPrice(e.getPrice());
                    }
                    cartItem.setMemberId(currentMember.getId());
                    cartItem.setCheckFlag(1);
                    cartItem.setProductId(e.getProductId());
                    cartItem.setProductSkuCode(e.getSkuCode());
                    cartItem.setProductSkuId(e.getId());
                    cartItem.setQuantity(e.getStock());
                    cartItem.setProductName(pmsProduct.getName());
                    cartItem.setProductCategoryId(pmsProduct.getProductCategoryId());
                    cartItem.setProductPic(e.getPic());
                    cartItem.setProductAttr(e.getSpData());
                    cartItem.setShipTo("");
                    total.addAndGet(this.cartItemService.add(cartItem));
                });

                productIdList.clear();
                skuCodeList.clear();
                orderNumMap.clear();
                productList.clear();
                pmsSkuStockList.clear();
                return CommonResult.success(total.get());
            }else{
                return CommonResult.failed("no this shopify order");
            }
        } catch (Exception e) {
            log.error("purchaseShopifyOrder,purchaseShopifyOrderParam[{}],error", purchaseShopifyOrderParam, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping(value = "/generateDeliveryOrder")
    @ApiOperation("客户的shopify订单生成我司发货订单")
    public CommonResult generateDeliveryOrder(PurchaseShopifyOrderParam purchaseShopifyOrderParam) {

        Assert.isTrue(null != purchaseShopifyOrderParam, "purchaseShopifyOrderParam null");
        Assert.isTrue(null != purchaseShopifyOrderParam.getShopifyOrderId() && purchaseShopifyOrderParam.getShopifyOrderId() > 0, "shopifyOrderNo null");
        Assert.isTrue(null != purchaseShopifyOrderParam.getShippingFrom() && purchaseShopifyOrderParam.getShippingFrom() >= 0 && purchaseShopifyOrderParam.getShippingFrom() < 2, "shippingFrom null");
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
                orderNumMap.put(tempPid + "_" + split[1].trim(), Integer.parseInt(split[2].trim()));
            });

            if (CollectionUtil.isNotEmpty(productIdList)) {

                QueryWrapper<XmsSourcingList> sourcingWrapper = new QueryWrapper<>();
                sourcingWrapper.lambda().eq(XmsSourcingList::getMemberId, currentMember.getId()).in(XmsSourcingList::getProductId, productIdList);
                List<XmsSourcingList> sourcingArrList = this.xmsSourcingListService.list(sourcingWrapper);
                if (CollectionUtil.isEmpty(sourcingArrList)) {
                    return CommonResult.failed("No data available");
                }
                // 组合需要的数据

                QueryWrapper<XmsCustomerSkuStock> stockWrapper = new QueryWrapper<>();
                stockWrapper.lambda().in(XmsCustomerSkuStock::getProductId, productIdList).in(XmsCustomerSkuStock::getSkuCode, skuCodeList).in(XmsCustomerSkuStock::getShippingFrom, purchaseShopifyOrderParam.getShippingFrom()).gt(XmsCustomerSkuStock::getStatus, 1);
                List<XmsCustomerSkuStock> skuStockList = this.xmsCustomerSkuStockService.list(stockWrapper);
                if (CollectionUtil.isEmpty(skuStockList)) {
                    return CommonResult.failed("No data available");
                }

                Map<String, XmsCustomerSkuStock> skuStockMap = new HashMap<>();
                skuStockList.forEach(e -> {
                    if(skuStockMap.containsKey(e.getProductId() + "_" + e.getSkuCode())){
                        XmsCustomerSkuStock tempStock = skuStockMap.get(e.getProductId() + "_" + e.getSkuCode());
                        tempStock.setStock(e.getStock() + tempStock.getStock());
                    } else{
                        skuStockMap.put(e.getProductId() + "_" + e.getSkuCode(), e);
                    }
                });

                List<XmsCustomerSkuStock> updateList = new ArrayList<>();
                List<XmsCustomStockLog> logList = new ArrayList<>();
                AtomicInteger total = new AtomicInteger();
                orderNumMap.forEach((k, v) -> {
                    if (!skuStockMap.containsKey(k) || skuStockMap.get(k).getStock() < v) {
                        total.getAndIncrement();
                    } else {
                        XmsCustomerSkuStock skuStock = skuStockMap.get(k);
                        skuStock.setStock(skuStock.getStock() - v);
                        updateList.add(skuStock);
                        XmsCustomStockLog tempLog = new XmsCustomStockLog();
                        BeanUtil.copyProperties(skuStock, tempLog);
                        tempLog.setId(null);
                        tempLog.setCreateTime(new Date());
                        tempLog.setUpdateTime(new Date());
                        logList.add(tempLog);
                    }
                });
                if (CollectionUtil.isNotEmpty(logList)) {
                    this.xmsCustomStockLogService.saveBatch(logList);
                    logList.clear();
                }

                // 批量添加库存日志
                if (total.get() > 0) {
                    // 存在没有匹配的数据
                    return CommonResult.failed("No data available or The number is not enough");
                }

                // 生成订单和订单详情信息
                OmsOrder order = new OmsOrder();
                order.setSourceType(1);
                order.setPayType(1);
                String orderNo = this.omsPortalOrderService.generateOrderSn(order);

                OrderPayParam orderPayParam = new OrderPayParam();
                BeanUtil.copyProperties(purchaseShopifyOrderParam, orderPayParam);
                GenerateOrderParam generateParam = GenerateOrderParam.builder().currentMember(currentMember)
                        .orderNo(orderNo).totalFreight(purchaseShopifyOrderParam.getShippingCostValue()).type(1).customerSkuStockList(updateList).orderPayParam(orderPayParam).build();
                // 确认库存数据，生成订单,然后扣库存
                GenerateOrderResult orderResult = this.orderUtils.generateDeliveryOrder(generateParam);
                return CommonResult.success(orderResult);

            } else {
                return CommonResult.failed("no this shopify order");
            }
        } catch (Exception e) {
            log.error("generateDeliveryOrder,purchaseShopifyOrderParam[{}],error", purchaseShopifyOrderParam, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation("shopify的发货订单List列表")
    @RequestMapping(value = "/deliverOrderList", method = RequestMethod.GET)
    public CommonResult deliverOrderList(XmsShopifyOrderinfoParam orderInfoParam) {

        Assert.notNull(orderInfoParam, "orderInfoParam null");
        Assert.isTrue(null != orderInfoParam.getDeliverOrderStatus(), "deliverOrderStatus null");

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            if (null == orderInfoParam.getPageNum() || orderInfoParam.getPageNum() == 0) {
                orderInfoParam.setPageNum(1);
            }
            if (null == orderInfoParam.getPageSize() || orderInfoParam.getPageSize() == 0) {
                orderInfoParam.setPageSize(5);
            }

            orderInfoParam.setShopifyName(currentMember.getShopifyName());
            CommonPage<OmsOrderDetail> list = this.omsPortalOrderService.list(orderInfoParam);
            return CommonResult.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("deliverOrderList,orderInfoParam[{}],error:", orderInfoParam, e);
            return CommonResult.failed("query failed");
        }
    }

    @PostMapping(value = "/createShopifyProductType")
    @ApiOperation("创建客户的shopify商品的type")
    public CommonResult createShopifyProductType(String productTypeName) {

        Assert.isTrue(StrUtil.isNotBlank(productTypeName), "productTypeName null");
        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            QueryWrapper<XmsShopifyProductType> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyProductType::getShopName, currentMember.getShopifyName()).eq(XmsShopifyProductType::getTypeName, productTypeName);
            int count = this.xmsShopifyProductTypeService.count(queryWrapper);
            if (count > 0) {
                return CommonResult.success("This name already exists");
            }
            XmsShopifyProductType shopifyProductType = new XmsShopifyProductType();
            shopifyProductType.setShopName(currentMember.getShopifyName());
            shopifyProductType.setTypeName(productTypeName);
            shopifyProductType.setCreateTime(new Date());
            boolean save = this.xmsShopifyProductTypeService.save(shopifyProductType);
            return CommonResult.success(save);
        } catch (Exception e) {
            log.error("createShopifyProductType,productType[{}],error", productTypeName, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @GetMapping(value = "/getShopifyProductType")
    @ApiOperation("获取客户的shopify商品的type")
    public CommonResult getShopifyProductType() {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            QueryWrapper<XmsShopifyProductType> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyProductType::getShopName, currentMember.getShopifyName());
            List<XmsShopifyProductType> list = this.xmsShopifyProductTypeService.list(queryWrapper);
            return CommonResult.success(list);
        } catch (Exception e) {
            log.error("getShopifyProductType,,error", e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping(value = "/createShopifyProductTag")
    @ApiOperation("创建客户的shopify商品的tag")
    public CommonResult createShopifyProductTag(String productTagName) {

        Assert.isTrue(StrUtil.isNotBlank(productTagName), "productTagName null");
        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            QueryWrapper<XmsShopifyProductTag> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyProductTag::getShopName, currentMember.getShopifyName()).eq(XmsShopifyProductTag::getTagName, productTagName);
            int count = this.xmsShopifyProductTagService.count(queryWrapper);
            if (count > 0) {
                return CommonResult.success("This name already exists");
            }
            XmsShopifyProductTag shopifyProductTag = new XmsShopifyProductTag();
            shopifyProductTag.setShopName(currentMember.getShopifyName());
            shopifyProductTag.setTagName(productTagName);
            shopifyProductTag.setCreateTime(new Date());
            boolean save = this.xmsShopifyProductTagService.save(shopifyProductTag);
            return CommonResult.success(save);
        } catch (Exception e) {
            log.error("createShopifyProductType,productTagName[{}],error", productTagName, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @GetMapping(value = "/getShopifyProductTag")
    @ApiOperation("获取客户的shopify商品的tag")
    public CommonResult getShopifyProductTag() {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            QueryWrapper<XmsShopifyProductTag> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyProductTag::getShopName, currentMember.getShopifyName());
            List<XmsShopifyProductTag> list = this.xmsShopifyProductTagService.list(queryWrapper);
            return CommonResult.success(list);
        } catch (Exception e) {
            log.error("getShopifyProductTag,,error", e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping(value = "/updateShopifyOrderAddress")
    @ApiOperation("更新客户的shopify地址信息")
    public CommonResult updateShopifyOrderAddress(XmsShopifyOrderAddressParam shopifyOrderAddress) {

        Assert.isTrue(null != shopifyOrderAddress, "shopifyOrderAddress null");
        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        Assert.isTrue(null != currentMember && currentMember.getId() > 0, "currentMember null");

        Assert.isTrue(null != shopifyOrderAddress.getId() && shopifyOrderAddress.getId() > 0, "id null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getFirstName()), "FirstName null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getCountry()), "Country null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getProvince()), "Province null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getLastName()), "LastName null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getCity()), "City null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getZip()), "Zip null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getAddress1()), "Address1 null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getAddress2()), "Address2 null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getPhone()), "Phone null");


        try {
            UpdateWrapper<XmsShopifyOrderAddress> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(XmsShopifyOrderAddress::getId, shopifyOrderAddress.getId())
                    .set(XmsShopifyOrderAddress::getFirstName, shopifyOrderAddress.getFirstName())
                    .set(XmsShopifyOrderAddress::getCountry, shopifyOrderAddress.getCountry())
                    .set(XmsShopifyOrderAddress::getProvince, shopifyOrderAddress.getProvince())
                    .set(XmsShopifyOrderAddress::getLastName, shopifyOrderAddress.getLastName())
                    .set(XmsShopifyOrderAddress::getCity, shopifyOrderAddress.getCity())
                    .set(XmsShopifyOrderAddress::getZip, shopifyOrderAddress.getZip())
                    .set(XmsShopifyOrderAddress::getAddress1, shopifyOrderAddress.getAddress1())
                    .set(XmsShopifyOrderAddress::getAddress2, shopifyOrderAddress.getAddress2())
                    .set(XmsShopifyOrderAddress::getPhone, shopifyOrderAddress.getPhone());
            boolean b = this.xmsShopifyOrderAddressService.update(null, updateWrapper);
            return CommonResult.success(b);
        } catch (Exception e) {
            log.error("updateShopifyOrderAddress,shopifyOrderAddress[{}],error", shopifyOrderAddress, e);
            return CommonResult.failed(e.getMessage());
        }
    }


}
