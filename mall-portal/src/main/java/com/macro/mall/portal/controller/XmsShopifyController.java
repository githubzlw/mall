package com.macro.mall.portal.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.macro.mall.portal.util.DigestUtils;
import com.macro.mall.portal.util.OrderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private UmsMemberCacheService umsMemberCacheService;
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
    @Autowired
    private IXmsShopifyFulfillmentService xmsShopifyFulfillmentService;
    @Autowired
    private IXmsShopifyFulfillmentItemService xmsShopifyFulfillmentItemService;
    @Autowired
    private IXmsShopifyLocationService xmsShopifyLocationService;
    @Autowired
    private IXmsCustomerProductService xmsCustomerProductService;
    @Autowired
    private IXmsShopifyPidInfoService xmsShopifyPidInfoService;

    @PostMapping(value = "/authorization")
    @ApiOperation("请求授权接口")
    public CommonResult authorization(@RequestParam("shopName") String shopName) {

        try {
            // 开放登录权限

            /*QueryWrapper<XmsShopifyAuth> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyAuth::getShopName, shopName).gt(XmsShopifyAuth::getMemberId, 0);
            List<XmsShopifyAuth> list = xmsShopifyAuthService.list(queryWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                list.clear();
                return CommonResult.existing("Already bind shop");
            }*/

            String uuid = RandomUtil.randomString(32);
            //this.shopMap.put(shopName, uuid);
            //请求授权
            JSONObject jsonObject = this.urlUtil.callUrlByGet(this.microServiceConfig.getShopifyUrl() + "/authuri?shop=" + shopName + "&uuid=" + uuid);
            CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);

            if (commonResult.getCode() == 200) {
                JSONObject dataJson = JSON.parseObject(commonResult.getData().toString());
                if (dataJson != null) {
                    String clientId = dataJson.getString("id");
                    System.err.println("--------------clientId:" + clientId);
                    String uri = dataJson.getString("uri");
                    this.redisUtil.hmsetObj(ShopifyConfig.SHOPIFY_KEY + uuid, "clientId", clientId, RedisUtil.EXPIRATION_TIME_1_DAY);
                    //this.shopMap.put(uuid, clientId);
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

        Assert.isTrue(StrUtil.isNotBlank(state), "state null");
        log.info("authCallback code:{},hmac:{},timestamp:{},state:{},shop:{},host:{}", code, hmac, timestamp, state, shop, host);
        String redirectUrl = "redirect:/apa/shopifyBindResult.html";

        Map<String, String[]> parameters = request.getParameterMap();
        String data = null;
        SortedSet<String> keys = new TreeSet<>(parameters.keySet());
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
        String email = "";
        try {
            UmsMember currentMember = null;
            try {
                currentMember = this.umsMemberService.getCurrentMember();
            } catch (Exception e) {
                e.printStackTrace();
            }

            rsMap.put("shopifyFlag", 0);
            rsMap.put("password", "");

            Object clientId = redisUtil.hmgetObj(ShopifyConfig.SHOPIFY_KEY + state, "clientId");
            // String clientId = this.shopMap.get(state);

            if (null == clientId || StringUtils.isBlank(clientId.toString()) || StringUtils.isBlank(shop)) {
                rsMap.put("result", "Please input shop name to authorize");
                redirectUrl = "redirect:/apa/product-shopify.html";
                rsMap.put("redirectUrl", redirectUrl);
                return CommonResult.failed(JSONObject.toJSONString(rsMap));
            }


            shop = shop.replace(ShopifyConfig.SHOPIFY_COM, "");
            /*SecretKeySpec keySpec = new SecretKeySpec(clientId.toString().getBytes(), ShopifyConfig.HMAC_ALGORITHM);
            Mac mac = Mac.getInstance(ShopifyConfig.HMAC_ALGORITHM);
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes());
*/
            String hmacsha256 = DigestUtils.HMACSHA256(data, clientId.toString());
            if (hmacsha256.equalsIgnoreCase(hmac)) {


                Map<String, String> mapParam = Maps.newHashMap();
                mapParam.put("shop", shop);
                mapParam.put("code", code);
                mapParam.put("uuid", state);
                if (null == currentMember || null == currentMember.getId() || currentMember.getId() <= 0) {
                    mapParam.put("userId", "0");
                } else {
                    mapParam.put("userId", String.valueOf(currentMember.getId()));
                }

                boolean noLogin = true;
                String token = "";
                JSONObject jsonObject = this.urlUtil.postURL(microServiceConfig.getShopifyUrl() + "/authGetToken", mapParam);
                CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);
                if (commonResult.getCode() == 200) {

                    log.info("authCallback,commonResult:[{}]", commonResult);
                    // 如果是没有登录的情况下，获取客户的邮箱，进行登录，然后绑定数据
                    if (null == currentMember || null == currentMember.getId() || currentMember.getId() <= 0) {
                        JSONObject shopifyUser = JSONObject.parseObject(commonResult.getData().toString()).getJSONObject("associated_user");
                        String userName = shopifyUser.getString("email");
                        this.umsMemberCacheService.delMember(userName);
                        // 没有注册，
                        currentMember = this.umsMemberService.getByUsernameNoCache(userName);
                        if (null == currentMember || null == currentMember.getId() || currentMember.getId() <= 0) {
                            // 注册
                            this.umsMemberService.registerNew(userName, "123456", "shopifyEmail", "10000", 0, 36);
                            currentMember = this.umsMemberService.getByUsernameNoCache(userName);
                            rsMap.put("password", "123456");
                        }
                    } else {
                        noLogin = false;
                    }

                    Long memberId = currentMember.getId();
                    if (!noLogin) {

                        UmsMember byId = this.umsMemberService.getById(currentMember.getId());
                        if (StrUtil.isNotBlank(byId.getShopifyName()) && !shop.equalsIgnoreCase(byId.getShopifyName())) {
                            // 如果存在原始店铺，并且和当前店铺不一致，则删除原始数据
                            this.deleteShopifyInfo(byId);
                        }


                        // 优先删除其他的授权token
                        QueryWrapper<XmsShopifyAuth> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda().eq(XmsShopifyAuth::getShopName, shop).notIn(XmsShopifyAuth::getUuid, state).nested(wrapper -> wrapper.eq(XmsShopifyAuth::getMemberId, 0).or().eq(XmsShopifyAuth::getMemberId, memberId));
                        this.xmsShopifyAuthService.remove(queryWrapper);

                        this.umsMemberService.updateShopifyInfo(currentMember.getId(), shop, 1);
                        currentMember.setShopifyFlag(1);
                        currentMember.setShopifyName(shop);
                        this.umsMemberService.updateSecurityContext();
                    }

                    // 检查和更新其他授权的当前店铺数据
                    this.changeShopifyInfo(memberId, shop);

                    // 异步同步shopify的数据
                    this.asyncShopifyInfo(currentMember.getId(), shop, currentMember.getUsername());
                    // 插入shopify的token
                    // ------------------
                    rsMap.put("shopifyName", shop);
                    rsMap.put("noLogin", noLogin ? 1 : 0);
                    rsMap.put("mail", currentMember.getUsername());
                    rsMap.put("shopifyFlag", 1);
                    rsMap.put("uuid", state);
                    // ------------------
                    // rsMap.put("redirectUrl", redirectUrl);
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
            if (StrUtil.isBlank(currentMember.getShopifyName())) {
                return CommonResult.failed("Please bind the store first");
            }
            orderinfoParam.setShopifyName(currentMember.getShopifyName());
            orderinfoParam.setMemberId(currentMember.getId());
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
            if (StrUtil.isBlank(currentMember.getShopifyName())) {
                return CommonResult.failed("Please bind the store first");
            }

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
            if (StrUtil.isBlank(currentMember.getShopifyName())) {
                return CommonResult.failed("Please bind the shopify store first");
            }
            QueryWrapper<XmsShopifyCollections> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyCollections::getShopName, currentMember.getShopifyName());
            List<XmsShopifyCollections> list = this.xmsShopifyCollectionsService.list(queryWrapper);
            if (CollectionUtil.isEmpty(list)) {
                Map<String, String> param = new HashMap<>();
                param.put("shopifyName", currentMember.getShopifyName());
                param.put("memberId", String.valueOf(currentMember.getId()));
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
    public CommonResult countryList(String flag) {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            if (StrUtil.isBlank(currentMember.getShopifyName())) {
                return CommonResult.failed("Please bind the shopify store first");
            }
            if (StrUtil.isNotBlank(flag)) {
                this.getCountryByShopifyName();
            }
            QueryWrapper<XmsShopifyCountry> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyCountry::getShopifyName, currentMember.getShopifyName())
                    .eq(XmsShopifyCountry::getMemberId, currentMember.getId());
            List<XmsShopifyCountry> list = xmsShopifyCountryService.list(queryWrapper);
            if (CollectionUtil.isEmpty(list) && StrUtil.isBlank(flag)) {
                this.getCountryByShopifyName();
                list = xmsShopifyCountryService.list(queryWrapper);
            }
            return CommonResult.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("countryList,currentMember[{}],error:", currentMember, e);
            return CommonResult.failed("query failed");
        }
    }

    @ApiOperation("获取shopify的Country列表")
    @RequestMapping(value = "/getCountryByShopifyName", method = RequestMethod.GET)
    public CommonResult getCountryByShopifyName() {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            UmsMember byId = this.umsMemberService.getById(currentMember.getId());
            if (StrUtil.isEmpty(byId.getShopifyName()) || 0 == byId.getShopifyFlag()) {
                return CommonResult.failed("Please bind the shopify store first");
            }

            Map<String, String> param = new HashMap<>();
            param.put("shopifyName", byId.getShopifyName());
            param.put("memberId", String.valueOf(byId.getId()));

            //请求数据
            JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/getCountryByShopifyName", param);
            return JSONObject.parseObject(jsonObject.toJSONString(), CommonResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getCountryByShopifyName,currentMember[{}],error:", currentMember, e);
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
    public CommonResult addToShopifyProducts(@RequestParam Long productId, @RequestParam String skuCodes, @RequestParam String collectionId, @RequestParam String productType, @RequestParam String productTags, @RequestParam Long sourcingId) {

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
            param.put("memberId", String.valueOf(currentMember.getId()));
            param.put("sourcingId", String.valueOf(sourcingId));

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

            param.put("shopifyName", byId.getShopifyName());
            param.put("memberId", String.valueOf(byId.getId()));


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
    public CommonResult getShopifyName(String shopifyName) {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            // 数据库判断是否绑定
            UmsMember byId = this.umsMemberService.getById(currentMember.getId());
            if(StrUtil.isNotBlank(byId.getShopifyName())){
                return CommonResult.success(1);
            }
            int byShopifyName = this.umsMemberService.getByShopifyName(shopifyName);
            if(byShopifyName == 0){
                return CommonResult.success(0);
            }
            return CommonResult.success(2);
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
            if (StrUtil.isBlank(byId.getShopifyName())) {
                return CommonResult.failed("Please bind the shopify store first");
            }
            this.deleteShopifyInfo(byId);
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
            if (StrUtil.isBlank(currentMember.getShopifyName())) {
                return CommonResult.failed("Please bind the shopify store first");
            }
            Map<String, String> param = new HashMap<>();
            param.put("shopifyName", currentMember.getShopifyName());
            param.put("memberId", String.valueOf(currentMember.getId()));

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
    @ApiImplicitParams({@ApiImplicitParam(name = "shopifyOrderNoId", value = "shopify的订单ID", required = true),
            @ApiImplicitParam(name = "transportType", value = "运输模式: 0 CHINA, 1 USA", required = true)})
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
                List<XmsShopifyPidInfo> pidInfoList = this.shopifyOrderinfoService.queryByShopifyLineItem(umsMember.getShopifyName(), collect, umsMember.getId());
                collect.clear();
                Map<Long, PmsProduct> pmsProductMap = new HashMap<>();// 存放商品信息
                Map<Long, List<XmsCustomerSkuStock>> skuStockMap = new HashMap<>();//存放购买库存信息
                //Map<Long, List<XmsCustomerSkuStock>> comStockMap = new HashMap<>();//存放累加购买库存信息
                Map<Long, Long> shopifyPidMap = new HashMap<>();
                if (CollectionUtil.isNotEmpty(pidInfoList)) {
                    List<Long> ids = new ArrayList<>();
                    pidInfoList.forEach(e -> {
                        ids.add(Long.parseLong(e.getPid()));
                        shopifyPidMap.put(Long.parseLong(e.getShopifyPid()), Long.parseLong(e.getPid()));
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
                    if (CollectionUtil.isNotEmpty(customerSkuStockList)) {
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

                    } else {
                        preOrderItem.setImg("");
                        preOrderItem.setPrice("0");
                        preOrderItem.setProductId(0L);
                        preOrderItem.setFreeStatus(0);
                        preOrderItem.setWeight(0D);
                        preOrderItem.setVolume(0D);
                    }
                    if (shopifyPidMap.containsKey(e.getProductId()) && finalSkuStockMap.containsKey(shopifyPidMap.get(e.getProductId()))) {
                        preOrderItem.setStockList(finalSkuStockMap.get(shopifyPidMap.get(e.getProductId())));
                    } else {
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

            if (CollectionUtil.isNotEmpty(productIdList)) {

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
                productList.forEach(e -> pmsProductMap.put(e.getId(), e));

                List<PmsSkuStock> pmsSkuStockList = new ArrayList<>();
                // 过滤未包含的skuCode并且赋值参数给的数量
                AtomicInteger total = new AtomicInteger();
                skuStockList.forEach(e -> {
                    if (!orderNumMap.containsKey(e.getProductId() + "_" + e.getSkuCode())) {
                        total.getAndIncrement();
                    } else {
                        e.setStock(orderNumMap.get(e.getProductId() + "_" + e.getSkuCode()));
                        pmsSkuStockList.add(e);
                    }
                });
                // 如果大于0,说明存在没有匹配上的数据
                if (total.get() > 0) {
                    return CommonResult.failed("Parameter data does not match");
                }
                if (CollectionUtil.isEmpty(pmsSkuStockList)) {
                    return CommonResult.failed("Insufficient inventory");
                }

                // 开始添加到购物车

                total.set(0);
                pmsSkuStockList.forEach(e -> {
                    PmsProduct pmsProduct = pmsProductMap.get(e.getProductId());
                    OmsCartItem cartItem = new OmsCartItem();
                    if (e.getStock() >= e.getMaxMoq()) {
                        cartItem.setPrice(e.getMaxPrice());
                    } else if (e.getStock() >= e.getMinMoq()) {
                        cartItem.setPrice(e.getMinPrice());
                    } else {
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
            } else {
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

            UmsMember usmBean = this.umsMemberService.getById(currentMember.getId());
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
                    if (skuStockMap.containsKey(e.getProductId() + "_" + e.getSkuCode())) {
                        XmsCustomerSkuStock tempStock = skuStockMap.get(e.getProductId() + "_" + e.getSkuCode());
                        tempStock.setStock(e.getStock() + tempStock.getStock());
                    } else {
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
                        .orderNo(orderNo).totalFreight(purchaseShopifyOrderParam.getShippingCostValue()).type(1).customerSkuStockList(updateList).orderPayParam(orderPayParam).shopifyOrderNo(byId.getOrderNo()).shippingFrom(purchaseShopifyOrderParam.getShippingFrom()).logoFlag(purchaseShopifyOrderParam.getLogoFlag()).logoUrl(usmBean.getLogoUrl()).build();
                // 确认库存数据，生成订单,然后扣库存
                GenerateOrderResult orderResult = this.orderUtils.generateDeliveryOrder(generateParam);
                // 生成订单成功后，更新shopify的order信息
                byId.setOurOrderId(orderResult.getOrderNoId());
                byId.setUpdateTime(new Date());
                this.shopifyOrderinfoService.saveOrUpdate(byId);
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
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getName()), "Name null");


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
                    .set(XmsShopifyOrderAddress::getPhone, shopifyOrderAddress.getPhone())
                    .set(XmsShopifyOrderAddress::getName, shopifyOrderAddress.getName());
            boolean b = this.xmsShopifyOrderAddressService.update(null, updateWrapper);
            return CommonResult.success(b);
        } catch (Exception e) {
            log.error("updateShopifyOrderAddress,shopifyOrderAddress[{}],error", shopifyOrderAddress, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @GetMapping(value = "/getFulfillments")
    @ApiOperation("获取运单信息")
    public CommonResult getFulfillments(FulfillmentParam fulfillmentParam) {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            if (null == fulfillmentParam.getPageNum() || fulfillmentParam.getPageNum() < 1) {
                fulfillmentParam.setPageNum(1);
            }
            if (null == fulfillmentParam.getPageSize() || fulfillmentParam.getPageSize() < 1) {
                fulfillmentParam.setPageSize(5);
            }
            if (StrUtil.isBlank(currentMember.getShopifyName())) {
                return CommonResult.failed("Please bind the store first");
            }
            fulfillmentParam.setShopifyName(currentMember.getShopifyName());
            Page<XmsShopifyFulfillment> tempPage = this.xmsShopifyFulfillmentService.list(fulfillmentParam);
            if (CollectionUtil.isNotEmpty(tempPage.getRecords())) {
                Page<FulfillmentOrder> rsPage = this.genNeedFulfillmentOrderResult(tempPage, fulfillmentParam);
                return CommonResult.success(rsPage);
            }
            return CommonResult.success(tempPage);
        } catch (Exception e) {
            log.error("getFulfillments,fulfillmentParam[{}],error", fulfillmentParam, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @GetMapping(value = "/getFulfillmentStatistics")
    @ApiOperation("获取运单统计")
    public CommonResult getFulfillmentStatistics(FulfillmentParam fulfillmentParam) {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            if (StrUtil.isBlank(currentMember.getShopifyName())) {
                return CommonResult.failed("Please bind the store first");
            }
            fulfillmentParam.setShopifyName(currentMember.getShopifyName());
            Map<String, Integer> rsMap = new HashMap<>();
            // beginTime
            if (StrUtil.isNotEmpty(fulfillmentParam.getBeginTime())) {
                fulfillmentParam.setBeginTime(fulfillmentParam.getBeginTime().substring(0, 10) + " 00:00:00");
            }
            // endTime
            if (StrUtil.isNotEmpty(fulfillmentParam.getEndTime())) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateTime = LocalDate.parse(fulfillmentParam.getEndTime().substring(0, 10), dateTimeFormatter);
                LocalDate plusDays = dateTime.plusDays(1);
                fulfillmentParam.setEndTime(plusDays.format(dateTimeFormatter) + " 00:00:00");
            }

            // all
            fulfillmentParam.setShipmentStatus(null);
            int allCount = this.xmsShopifyFulfillmentService.getFulfillmentStatistics(fulfillmentParam);
            rsMap.put("allCount", allCount);

            // in transit出运中 pickup 待收货 delivered已签收 expired超期 undelivered未收到 other其他
            fulfillmentParam.setShipmentStatus("in transit");
            int inTransitCount = this.xmsShopifyFulfillmentService.getFulfillmentStatistics(fulfillmentParam);
            rsMap.put("inTransitCount", inTransitCount);

            fulfillmentParam.setShipmentStatus("pickup");
            int pickupCount = this.xmsShopifyFulfillmentService.getFulfillmentStatistics(fulfillmentParam);
            rsMap.put("pickupCount", pickupCount);


            fulfillmentParam.setShipmentStatus("delivered");
            int deliveredCount = this.xmsShopifyFulfillmentService.getFulfillmentStatistics(fulfillmentParam);
            rsMap.put("deliveredCount", deliveredCount);

            fulfillmentParam.setShipmentStatus("expired");
            int expiredCount = this.xmsShopifyFulfillmentService.getFulfillmentStatistics(fulfillmentParam);
            rsMap.put("expiredCount", expiredCount);

            fulfillmentParam.setShipmentStatus("undelivered");
            int undeliveredCount = this.xmsShopifyFulfillmentService.getFulfillmentStatistics(fulfillmentParam);
            rsMap.put("undeliveredCount", undeliveredCount);

            fulfillmentParam.setShipmentStatus("other");
            int otherCount = this.xmsShopifyFulfillmentService.getFulfillmentStatistics(fulfillmentParam);
            rsMap.put("otherCount", otherCount);

            return CommonResult.success(rsMap);
        } catch (Exception e) {
            log.error("getFulfillmentStatistics,,error", e);
            return CommonResult.failed(e.getMessage());
        }
    }


    private Page<FulfillmentOrder> genNeedFulfillmentOrderResult(Page<XmsShopifyFulfillment> tempPage, FulfillmentParam fulfillmentParam) {

        // 1.整合全部的订单数据，获取订单地址和订单详情信息

        List<Long> orderNoList = new ArrayList<>();
        List<String> tempNoList = new ArrayList<>();
        List<FulfillmentOrder> fulfillmentOrderList = new ArrayList<>();
        tempPage.getRecords().forEach(e -> {
            orderNoList.add(e.getOrderId());
            tempNoList.add(String.valueOf(e.getOrderId()));
            FulfillmentOrder tempRs = new FulfillmentOrder();
            BeanUtil.copyProperties(e, tempRs);
            if (StrUtil.isBlank(tempRs.getShipmentStatus())) {
                tempRs.setShipmentStatus("");
            }
            fulfillmentOrderList.add(tempRs);
        });

        QueryWrapper<XmsShopifyOrderAddress> addressWrapper = new QueryWrapper<>();
        addressWrapper.lambda().in(XmsShopifyOrderAddress::getOrderNo, orderNoList);
        List<XmsShopifyOrderAddress> addressList = this.xmsShopifyOrderAddressService.list(addressWrapper);
        Map<Long, XmsShopifyOrderAddress> addressMap = new HashMap<>();
        addressList.forEach(e -> addressMap.put(e.getOrderNo(), e));


        QueryWrapper<XmsShopifyOrderDetails> detailsWrapper = new QueryWrapper<>();
        detailsWrapper.lambda().in(XmsShopifyOrderDetails::getOrderNo, orderNoList);
        List<XmsShopifyOrderDetails> detailsList = this.xmsShopifyOrderDetailsService.list(detailsWrapper);
        Map<Long, List<XmsShopifyOrderDetails>> dtMap = detailsList.stream().collect(Collectors.groupingBy(XmsShopifyOrderDetails::getOrderNo));

        QueryWrapper<OmsOrder> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.lambda().in(OmsOrder::getShopifyOrderNo, orderNoList);


        Map<Long, List<ShopifyOrderDetailsShort>> shortMap = new HashMap<>();
        dtMap.forEach((k, v) -> {
            if (CollectionUtil.isNotEmpty(v)) {
                List<ShopifyOrderDetailsShort> tempList = new ArrayList<>();
                v.forEach(cl -> {
                    ShopifyOrderDetailsShort tempShort = new ShopifyOrderDetailsShort();
                    BeanUtil.copyProperties(cl, tempShort);
                    tempList.add(tempShort);
                });
                shortMap.put(k, tempList);
            }
        });


        this.shopifyOrderinfoService.dealShopifyOrderDetailsMainImg(shortMap);

        addressList.clear();
        dtMap.clear();
        detailsList.clear();

        List<OmsOrder> omsOrders = this.orderUtils.queryByList(tempNoList);
        Map<Long, String> tempMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(omsOrders)) {
            omsOrders.forEach(e -> tempMap.put(Long.parseLong(e.getShopifyOrderNo()), e.getOrderSn()));
            omsOrders.clear();
        }

        // 2.组合 运单，订单地址，订单详情  数据
        fulfillmentOrderList.forEach(e -> {
            e.setOrderAddress(addressMap.get(e.getOrderId()));
            e.setItemList(shortMap.get(e.getOrderId()));
            e.setOurOrderNo(tempMap.getOrDefault(e.getOrderId(), ""));
            e.setTrackingNumberUrl(StrUtil.isNotBlank(e.getTrackingNumber()) ? "https://www.17track.net/en#nums=" + e.getTrackingNumber() : "");
        });

        tempMap.clear();

        addressMap.clear();
        shortMap.clear();

        Page<FulfillmentOrder> rsPage = new Page<>(fulfillmentParam.getPageNum(), fulfillmentParam.getPageSize());
        rsPage.setTotal(tempPage.getTotal());
        rsPage.setSize(tempPage.getSize());
        rsPage.setPages(tempPage.getPages());
        rsPage.setRecords(fulfillmentOrderList);
        return rsPage;
    }


    @GetMapping(value = "/getFulfillmentItems")
    @ApiOperation("获取运单详情信息")
    public CommonResult getFulfillmentItems(FulfillmentParam fulfillmentParam) {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            if (null == fulfillmentParam.getPageNum() || fulfillmentParam.getPageNum() < 1) {
                fulfillmentParam.setPageNum(1);
            }
            if (null == fulfillmentParam.getPageSize() || fulfillmentParam.getPageSize() < 1) {
                fulfillmentParam.setPageSize(5);
            }
            if (StrUtil.isBlank(currentMember.getShopifyName())) {
                return CommonResult.failed("Please bind the store first");
            }
            if (StrUtil.isBlank(fulfillmentParam.getTrackingNumber())) {
                fulfillmentParam.setTrackingNumber(null);
            }
            if (StrUtil.isBlank(fulfillmentParam.getBeginTime())) {
                fulfillmentParam.setBeginTime(null);
            }
            if (StrUtil.isBlank(fulfillmentParam.getEndTime())) {
                fulfillmentParam.setEndTime(null);
            }
            if (StrUtil.isBlank(fulfillmentParam.getCountry())) {
                fulfillmentParam.setCountry(null);
            }
            if (StrUtil.isBlank(fulfillmentParam.getTitle())) {
                fulfillmentParam.setTitle(null);
            }

            fulfillmentParam.setShopifyName(currentMember.getShopifyName());
            int count = this.xmsShopifyFulfillmentItemService.queryShopifyOrderItemsCount(fulfillmentParam);
            Page<FulfillmentOrderItem> rsPage = new Page<>(fulfillmentParam.getPageNum(), fulfillmentParam.getPageSize());
            if (count > 0) {
                List<FulfillmentOrderItem> itemList = this.xmsShopifyFulfillmentItemService.queryShopifyOrderItems(fulfillmentParam);

                List<Long> orderNoList = new ArrayList<>();
                Map<Long, FulfillmentOrderItem> orderItemMap = new HashMap<>();

                itemList.forEach(e -> {
                    orderNoList.add(e.getOrderNo());
                    orderItemMap.put(e.getOrderNo(), e);
                    e.setShipFrom(-1);
                });

                this.shopifyOrderinfoService.dealItemImg(itemList);


                if (CollectionUtil.isNotEmpty(orderNoList)) {
                    QueryWrapper<XmsShopifyOrderinfo> orderinfoWrapper = new QueryWrapper<>();
                    orderinfoWrapper.lambda().in(XmsShopifyOrderinfo::getOrderNo, orderNoList);
                    List<XmsShopifyOrderinfo> shopifyOrderinfoList = this.shopifyOrderinfoService.list(orderinfoWrapper);


                    Map<Long, Long> ourOrderIdAndOrderNoMap = new HashMap<>();
                    List<Long> ourOrderIdList = new ArrayList<>();
                    shopifyOrderinfoList.forEach(e -> {
                        ourOrderIdList.add(e.getOurOrderId());
                        ourOrderIdAndOrderNoMap.put(e.getOurOrderId(), e.getOrderNo());
                    });

                    if (CollectionUtil.isNotEmpty(ourOrderIdList)) {
                        List<OmsOrder> omsOrders = this.omsPortalOrderService.queryByOrderIdList(ourOrderIdList);
                        Map<Long, Integer> orderNoAndShipFromMap = new HashMap<>();
                        if (CollectionUtil.isNotEmpty(omsOrders)) {
                            omsOrders.forEach(e -> orderNoAndShipFromMap.put(ourOrderIdAndOrderNoMap.get(e.getId()), e.getShippingFrom()));
                            omsOrders.clear();
                        }
                        if (orderNoAndShipFromMap.size() > 0) {
                            orderItemMap.forEach((k, v) -> v.setShipFrom(orderNoAndShipFromMap.getOrDefault(k, -1)));
                            orderNoAndShipFromMap.clear();
                        }

                    }

                    orderNoList.clear();
                    ourOrderIdAndOrderNoMap.clear();

                    shopifyOrderinfoList.clear();

                }

                rsPage.setTotal(count);
                rsPage.setSize(itemList.size());
                int page = count / fulfillmentParam.getPageSize();
                if (count % fulfillmentParam.getPageSize() > 0) {
                    page++;
                }
                rsPage.setPages(page);
                rsPage.setRecords(itemList);
            }
            return CommonResult.success(rsPage);
        } catch (Exception e) {
            log.error("getFulfillments,fulfillmentParam[{}],error", fulfillmentParam, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping(value = "/createShopifyOrderAddress")
    @ApiOperation("创建客户的shopify地址信息")
    public CommonResult createShopifyOrderAddress(XmsShopifyOrderAddressParam shopifyOrderAddress) {

        Assert.isTrue(null != shopifyOrderAddress, "shopifyOrderAddress null");
        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        Assert.isTrue(null != currentMember && currentMember.getId() > 0, "currentMember null");

        Assert.isTrue(null != shopifyOrderAddress.getOrderNo() && shopifyOrderAddress.getOrderNo() > 0, "orderNo null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getFirstName()), "FirstName null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getCountry()), "Country null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getProvince()), "Province null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getLastName()), "LastName null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getCity()), "City null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getZip()), "Zip null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getAddress1()), "Address1 null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getAddress2()), "Address2 null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getPhone()), "Phone null");
        Assert.isTrue(StrUtil.isNotBlank(shopifyOrderAddress.getName()), "Name null");


        try {
            XmsShopifyOrderAddress orderAddress = new XmsShopifyOrderAddress();
            BeanUtil.copyProperties(shopifyOrderAddress, orderAddress);
            orderAddress.setId(null);
            this.xmsShopifyOrderAddressService.save(orderAddress);
            return CommonResult.success(orderAddress);
        } catch (Exception e) {
            log.error("createShopifyOrderAddress,shopifyOrderAddress[{}],error", shopifyOrderAddress, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @PostMapping(value = "/cancelOrderByShopifyName")
    @ApiOperation("根据shopifyName取消订单")
    public CommonResult cancelOrderByShopifyName(String shopifyName, String orderNo) {

        Assert.isTrue(StrUtil.isNotBlank(shopifyName), "shopifyName null");
        Assert.isTrue(StrUtil.isNotBlank(orderNo), "orderNo null");

        try {
            UmsMember currentMember = this.umsMemberService.getCurrentMember();
            Map<String, String> param = new HashMap<>();
            param.put("shopifyName", shopifyName);
            param.put("orderNo", orderNo);
            param.put("memberId", String.valueOf(currentMember.getId()));
            JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/cancelOrderByShopifyName", param);
            /*if(200 == commonResult.getCode()){
                 this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/getOrdersByShopifyName", param);
            }*/
            return JSON.toJavaObject(jsonObject, CommonResult.class);
        } catch (Exception e) {
            log.error("cancelOrderByShopifyName,shopifyName[{}],orderNo[{}],error", shopifyName, orderNo, e);
            return CommonResult.failed(e.getMessage());
        }
    }


    @GetMapping(value = "/checkShopifySourcingStatus")
    @ApiOperation("检查在shopify的商品sourcingList的状态")
    public CommonResult checkShopifySourcingStatus(Long sourcingId, Long productId) {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        Assert.isTrue(null != sourcingId && sourcingId > 0, "sourcingId null");
        Assert.isTrue(null != productId && productId > 0, "productId null");
        try {
            XmsSourcingList byId = this.xmsSourcingListService.getById(sourcingId);
            if (null == byId) {
                return CommonResult.validateFailed("no this data");
            }
            if (null == byId.getStatus() || 2 != byId.getStatus()) {
                return CommonResult.validateFailed("The data is not processed properly");
            }
            if (!productId.equals(byId.getProductId())) {
                return CommonResult.validateFailed("Pid mismatch");
            }
            return CommonResult.success("OYX");
        } catch (Exception e) {
            log.error("checkShopifySourcingStatus,sourcingId[{}],productId[{}],error", sourcingId, productId, e);
            return CommonResult.failed("checkShopifySourcingStatus error!");
        }
    }

    @GetMapping(value = "/getLocationList")
    @ApiOperation("获取店铺的location列表")
    public CommonResult getLocationList() {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            if (StrUtil.isBlank(currentMember.getShopifyName())) {
                return CommonResult.failed("Please bind the store first");
            }

            QueryWrapper<XmsShopifyLocation> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyLocation::getMemberId, currentMember.getId())
                    .eq(XmsShopifyLocation::getShopifyName, currentMember.getShopifyName());

            List<XmsShopifyLocation> list = this.xmsShopifyLocationService.list(queryWrapper);

            if (CollectionUtil.isEmpty(list)) {
                Map<String, String> param = new HashMap<>();

                param.put("shopifyName", currentMember.getShopifyName());
                param.put("memberId", String.valueOf(currentMember.getId()));

                //请求数据
                JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/getLocationByShopifyName", param);
                CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);
                if (null != commonResult && commonResult.getCode() == 200) {
                    list = this.xmsShopifyLocationService.list(queryWrapper);
                }
            }
            return CommonResult.success(list);
        } catch (Exception e) {
            log.error("getLocationList,currentMember[{}],error", currentMember, e);
            return CommonResult.failed("getLocationList error!");
        }
    }


    @ApiOperation("shopify的物流信息")
    @RequestMapping(value = "/logisticsInformation", method = RequestMethod.POST)
    public CommonResult logisticsInformation(Long orderNo) {

        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {
            QueryWrapper<XmsShopifyFulfillment> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyFulfillment::getOrderId, orderNo).isNotNull(XmsShopifyFulfillment::getTrackingNumber);
            List<XmsShopifyFulfillment> list = this.xmsShopifyFulfillmentService.list(queryWrapper);
            if (CollectionUtil.isEmpty(list)) {
                //请求数据
                Map<String, String> param = new HashMap<>();

                param.put("shopifyName", currentMember.getShopifyName());
                param.put("orders", String.valueOf(orderNo));
                param.put("memberId", String.valueOf(currentMember.getId()));

                JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/getFulfillmentByShopifyName", param);
                CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);
                if (null != commonResult && commonResult.getCode() == 200) {
                    list = this.xmsShopifyFulfillmentService.list(queryWrapper);
                }
            }
            return CommonResult.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("logisticsInformation,orderNo[{}],error:", orderNo, e);
            return CommonResult.failed("logisticsInformation query failed");
        }
    }

    @PostMapping("/createFulfillment")
    @ApiOperation("创建履行订单")
    public CommonResult createFulfillment(FulfillmentParam fulfillmentParam) {

        Assert.notNull(fulfillmentParam, "fulfillmentParam is null");
        Assert.isTrue(null != fulfillmentParam.getOrderNo() && fulfillmentParam.getOrderNo() > 0, "orderNo is null");
        Assert.isTrue(StrUtil.isNotBlank(fulfillmentParam.getTrackingNumber()), "trackingNumber is null");
        // Assert.isTrue(StrUtil.isNotBlank(fulfillmentParam.getTrackingCompany()), "trackingCompany is null");


        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {

            if (StrUtil.isBlank(currentMember.getShopifyName())) {
                return CommonResult.failed("Please bind the shopify store first");
            }

            Map<String, String> param = new HashMap<>();

            param.put("shopifyName", currentMember.getShopifyName());
            param.put("orderNo", String.valueOf(fulfillmentParam.getOrderNo()));
            param.put("trackingNumber", fulfillmentParam.getTrackingNumber());
            param.put("trackingCompany", fulfillmentParam.getTrackingCompany());
            param.put("memberId", String.valueOf(currentMember.getId()));
            param.put("locationId", fulfillmentParam.getLocationId());

            JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/createFulfillment", param);
            CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);
            if (null != commonResult && commonResult.getCode() == 200) {

                // 执行成功后，再查询一次
                Map<String, String> rmParam = new HashMap<>();

                rmParam.put("shopifyName", currentMember.getShopifyName());
                rmParam.put("orders", String.valueOf(fulfillmentParam.getOrderNo()));
                rmParam.put("memberId", String.valueOf(currentMember.getId()));
                this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/getFulfillmentByShopifyName", rmParam);
            }
            return commonResult;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("createFulfillment fulfillmentParam:[{}],error:", fulfillmentParam, e);
            return CommonResult.failed("createFulfillment" + ",error!");
        }
    }

    @PostMapping("/createFulfillment2")
    @ApiOperation("创建履行订单2")
    public CommonResult createFulfillment2(FulfillmentParam fulfillmentParam) {

        Assert.notNull(fulfillmentParam, "fulfillmentParam is null");
        Assert.isTrue(null != fulfillmentParam.getOrderNo() && fulfillmentParam.getOrderNo() > 0, "orderNo is null");
        Assert.isTrue(StrUtil.isNotBlank(fulfillmentParam.getTrackingNumber()), "trackingNumber is null");
        // Assert.isTrue(StrUtil.isNotBlank(fulfillmentParam.getTrackingCompany()), "trackingCompany is null");


        UmsMember currentMember = this.umsMemberService.getCurrentMember();
        try {

            if (StrUtil.isBlank(currentMember.getShopifyName())) {
                return CommonResult.failed("Please bind the shopify store first");
            }

            Map<String, String> param = new HashMap<>();

            param.put("shopifyName", currentMember.getShopifyName());
            param.put("orderNo", String.valueOf(fulfillmentParam.getOrderNo()));
            param.put("trackingNumber", fulfillmentParam.getTrackingNumber());
            param.put("trackingCompany", fulfillmentParam.getTrackingCompany());
            param.put("memberId", String.valueOf(currentMember.getId()));
            param.put("notifyCustomer", String.valueOf(fulfillmentParam.isNotifyCustomer()));
            param.put("message", fulfillmentParam.getMessage());

            JSONObject jsonObject = this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/createFulfillment2", param);
            CommonResult commonResult = JSON.toJavaObject(jsonObject, CommonResult.class);
            if (null != commonResult && commonResult.getCode() == 200) {
                // 执行成功后，再查询一次
                Map<String, String> rmParam = new HashMap<>();

                rmParam.put("shopifyName", currentMember.getShopifyName());
                rmParam.put("orders", String.valueOf(fulfillmentParam.getOrderNo()));
                rmParam.put("memberId", String.valueOf(currentMember.getId()));
                this.urlUtil.postURL(this.microServiceConfig.getShopifyUrl() + "/getFulfillmentByShopifyName", rmParam);
            }
            return commonResult;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("createFulfillment fulfillmentParam:[{}],error:", fulfillmentParam, e);
            return CommonResult.failed("createFulfillment" + ",error!");
        }
    }


    /**
     * 检查多账号绑定店铺的数据
     *
     * @param memberId
     * @param shopifyName
     */
    private void changeShopifyInfo(Long memberId, String shopifyName) {

        /**
         * 多账号绑定同一个店铺
         * 新的账号绑定已有店铺，提示店铺已被绑定，如果强行绑定，会强制取消上一个绑定账号内店铺的所有内容，包括商品和订单，并将上一个绑定账号内的商品和订单转移到新的账号上
         * 老账号状态类似解绑店铺
         * 新账号状态copy上一个绑定账号
         */

        System.err.println("updateShopifyInfo:" + memberId + "," + shopifyName);
        try {
            QueryWrapper<XmsShopifyAuth> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyAuth::getShopName, shopifyName).notIn(XmsShopifyAuth::getMemberId, memberId);
            List<XmsShopifyAuth> list = this.xmsShopifyAuthService.list(queryWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                List<Long> memberList = list.stream().filter(e -> null != e.getMemberId() && e.getMemberId() > 0).mapToLong(XmsShopifyAuth::getMemberId).boxed().collect(Collectors.toList());

                // 更新your live product下所有商品
                QueryWrapper<XmsCustomerProduct> customerProductWrapper = new QueryWrapper<>();
                customerProductWrapper.lambda()
                        .notIn(XmsCustomerProduct::getMemberId, memberId)
                        .eq(XmsCustomerProduct::getShopifyName, shopifyName)
                        .in(XmsCustomerProduct::getMemberId, memberList);
                this.xmsCustomerProductService.remove(customerProductWrapper);

                // 更新shopify order下所有订单
                QueryWrapper<XmsShopifyOrderinfo> shopifyOrderinfoWrapper = new QueryWrapper<>();
                shopifyOrderinfoWrapper.lambda()
                        .notIn(XmsShopifyOrderinfo::getMemberId, memberId)
                        .in(XmsShopifyOrderinfo::getMemberId, memberList)
                        .eq(XmsShopifyOrderinfo::getShopifyName, shopifyName);
                this.shopifyOrderinfoService.remove(shopifyOrderinfoWrapper);


                // pid关联关系更新
                UpdateWrapper<XmsShopifyPidInfo> shopifyPidInfoWrapper = new UpdateWrapper<>();
                shopifyPidInfoWrapper.lambda()
                        .set(XmsShopifyPidInfo::getMemberId, memberId)
                        .eq(XmsShopifyPidInfo::getShopifyName, shopifyName)
                        .in(XmsShopifyPidInfo::getMemberId, memberList);
                this.xmsShopifyPidInfoService.update(shopifyPidInfoWrapper);

                //System.err.println(Arrays.toString(Thread.currentThread().getStackTrace()));

                System.err.println("------delete shopifyName:" + shopifyName + ";memberId:" + memberId);
                // 删除其他授权token
                QueryWrapper<XmsShopifyAuth> authQueryWrapper = new QueryWrapper<>();
                authQueryWrapper.lambda().eq(XmsShopifyAuth::getShopName, shopifyName).notIn(XmsShopifyAuth::getMemberId, memberId);
                this.xmsShopifyAuthService.remove(authQueryWrapper);
                // 更新客户信息的其他此店铺
                this.umsMemberService.clearOtherShopifyInfo(memberId, shopifyName);


                list.clear();
            }

            System.err.println("updateShopifyInfo:" + memberId + "," + shopifyName + "--------end");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("updateShopifyInfo,memberId[{}],shopifyName[{}],error:", memberId, shopifyName, e);
        }


    }

    /**
     * 删除店铺数据
     *
     * @param byId
     */
    private void deleteShopifyInfo(UmsMember byId) {

        /**
         * 解绑 需要强提醒用户，解绑店铺会清除与店铺相关的所有内容，包括商品和订单等
         * 清除your live product下所有商品
         * 清除shopify order下所有订单
         * recommend下商品状态全部变为not imported状态
         * sourcing list下所有success状态的商品在more actions中恢复add to my live product选项
         */

        System.err.println("deleteShopifyInfo:" + byId.getId() + "," + byId.getShopifyName());
        try {

            // 清除your live product下所有商品
            QueryWrapper<XmsCustomerProduct> customerProductWrapper = new QueryWrapper<>();
            customerProductWrapper.lambda().eq(XmsCustomerProduct::getShopifyName, byId.getShopifyName())
                    .eq(XmsCustomerProduct::getMemberId, byId.getId());


            List<XmsCustomerProduct> productList = this.xmsCustomerProductService.list(customerProductWrapper);

            if (CollectionUtil.isNotEmpty(productList)) {
                List<Long> sourcingIdsList = productList.stream().mapToLong(XmsCustomerProduct::getSourcingId).boxed().collect(Collectors.toList());
                List<Long> productIdsList = productList.stream().mapToLong(XmsCustomerProduct::getProductId).boxed().collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(sourcingIdsList) && CollectionUtil.isNotEmpty(productIdsList)) {
                    UpdateWrapper<XmsSourcingList> sourcingUpdateWrapper = new UpdateWrapper<>();
                    sourcingUpdateWrapper.lambda().set(XmsSourcingList::getAddProductFlag, 0)
                            .in(XmsSourcingList::getId, sourcingIdsList)
                            .in(XmsSourcingList::getProductId, productIdsList)
                            .eq(XmsSourcingList::getMemberId, byId.getId());
                    this.xmsSourcingListService.update(sourcingUpdateWrapper);
                    sourcingIdsList.clear();
                    productIdsList.clear();
                }
                productList.clear();
            }

            this.xmsCustomerProductService.remove(customerProductWrapper);

            // 清除shopify order下所有订单
            QueryWrapper<XmsShopifyOrderinfo> shopifyOrderinfoWrapper = new QueryWrapper<>();
            shopifyOrderinfoWrapper.lambda().eq(XmsShopifyOrderinfo::getMemberId, byId.getId())
                    .eq(XmsShopifyOrderinfo::getShopifyName, byId.getShopifyName());
            List<XmsShopifyOrderinfo> orderinfoList = this.shopifyOrderinfoService.list(shopifyOrderinfoWrapper);

            if (CollectionUtil.isNotEmpty(orderinfoList)) {
                List<Long> longList = orderinfoList.stream().mapToLong(XmsShopifyOrderinfo::getOrderNo).boxed().collect(Collectors.toList());

                QueryWrapper<XmsShopifyOrderDetails> orderDetailsWrapper = new QueryWrapper<>();
                orderDetailsWrapper.lambda().in(XmsShopifyOrderDetails::getOrderNo, longList);
                this.xmsShopifyOrderDetailsService.remove(orderDetailsWrapper);

                QueryWrapper<XmsShopifyOrderAddress> orderAddressWrapper = new QueryWrapper<>();
                orderAddressWrapper.lambda().in(XmsShopifyOrderAddress::getOrderNo, longList);
                this.xmsShopifyOrderAddressService.remove(orderAddressWrapper);

                orderinfoList.clear();
                longList.clear();
            }

            this.shopifyOrderinfoService.remove(shopifyOrderinfoWrapper);


            // sourcing list下所有success状态的商品在more actions中恢复add to my live product选项
            QueryWrapper<XmsShopifyPidInfo> shopifyPidInfoWrapper = new QueryWrapper<>();
            shopifyPidInfoWrapper.lambda().eq(XmsShopifyPidInfo::getShopifyName, byId.getShopifyName())
                    .eq(XmsShopifyPidInfo::getMemberId, byId.getId());

            // List<XmsShopifyPidInfo> pidInfoList = this.xmsShopifyPidInfoService.list(shopifyPidInfoWrapper);


            // 删除shopify的PID关联关系, recommend下商品状态全部变为not imported状态
            this.xmsShopifyPidInfoService.remove(shopifyPidInfoWrapper);

            // 删除授权信息
            QueryWrapper<XmsShopifyAuth> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyAuth::getShopName, byId.getShopifyName()).eq(XmsShopifyAuth::getMemberId, byId.getId());
            this.xmsShopifyAuthService.remove(queryWrapper);

            // 更新客户的绑定信息
            this.umsMemberService.updateShopifyInfo(byId.getId(), "", 0);

            // 刷新缓存
            this.umsMemberService.updateSecurityContext();

            System.err.println("deleteShopifyInfo:" + byId.getId() + "," + byId.getShopifyName() + "-------end");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("deleteShopifyInfo,byId[{}],error:", byId, e);
        }
    }


    private void asyncShopifyInfo(Long memberId, String shopifyName, String userName) {
        try {

            String url = microServiceConfig.getShopifyUrl().replace("/shopify", "/shopifyTask") + "/syncInfoByShopifyName";

            List<ShopifyTaskBean> taskBeanList = new ArrayList<>();
            System.err.println(JSONObject.toJSONString(taskBeanList));
            Map<String, String> param = new HashMap<>();
            param.put("listParam", JSONObject.toJSONString(taskBeanList));
            JSONObject jsonObject = this.urlUtil.postURL(url, param);
            CommonResult commonResult = JSONObject.parseObject(jsonObject.toJSONString(), CommonResult.class);
            if (null == commonResult || commonResult.getCode() != 200) {
                TimeUnit.SECONDS.sleep(3);
                jsonObject = this.urlUtil.postURL(url, param);
                commonResult = JSONObject.parseObject(jsonObject.toJSONString(), CommonResult.class);
            }
            if (null == commonResult || commonResult.getCode() != 200) {
                TimeUnit.SECONDS.sleep(3);
                jsonObject = this.urlUtil.postURL(url, param);
                commonResult = JSONObject.parseObject(jsonObject.toJSONString(), CommonResult.class);
            }
            System.err.println("asyncShopifyInfo:" + JSONObject.toJSONString(commonResult));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("asyncShopifyInfo,memberId:[{}],shopifyName:[{}],error:", memberId, shopifyName, e);
        }
    }

}
