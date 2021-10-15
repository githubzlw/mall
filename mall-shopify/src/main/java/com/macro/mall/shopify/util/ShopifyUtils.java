package com.macro.mall.shopify.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.Gson;
import com.macro.mall.common.util.BigDecimalUtil;
import com.macro.mall.entity.*;
import com.macro.mall.mapper.XmsCustomerProductMapper;
import com.macro.mall.mapper.XmsCustomerSkuStockMapper;
import com.macro.mall.mapper.XmsShopifyPidInfoMapper;
import com.macro.mall.mapper.XmsSourcingListMapper;
import com.macro.mall.shopify.config.ShopifyConfig;
import com.macro.mall.shopify.config.ShopifyRestTemplate;
import com.macro.mall.shopify.pojo.FulfillmentParam;
import com.macro.mall.shopify.pojo.FulfillmentStatusEnum;
import com.macro.mall.shopify.pojo.LogisticsCompanyEnum;
import com.macro.mall.shopify.pojo.orders.Line_items;
import com.macro.mall.shopify.pojo.orders.Orders;
import com.macro.mall.shopify.pojo.orders.OrdersWraper;
import com.macro.mall.shopify.pojo.orders.Shipping_address;
import com.macro.mall.shopify.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.shopify.util
 * @date:2021-05-12
 */
@Service
@Slf4j
public class ShopifyUtils {

    @Autowired
    private ShopifyRestTemplate shopifyRestTemplate;
    @Autowired
    private ShopifyConfig shopifyConfig;
    @Autowired
    private IXmsShopifyAuthService xmsShopifyAuthService;
    @Autowired
    private IXmsShopifyOrderinfoService shopifyOrderinfoService;
    @Autowired
    private IXmsShopifyOrderDetailsService shopifyOrderDetailsService;
    @Autowired
    private IXmsShopifyOrderAddressService shopifyOrderAddressService;
    @Autowired
    private IXmsShopifyCollectionsService xmsShopifyCollectionsService;
    @Autowired
    private IXmsCustomerProductService customerProductService;

    @Autowired
    private IXmsSourcingListService sourcingListService;
    @Autowired
    private IXmsShopifyCountryService xmsShopifyCountryService;
    @Autowired
    private XmsShopifyPidInfoMapper xmsShopifyPidInfoMapper;
    @Autowired
    private XmsSourcingListMapper xmsSourcingListMapper;
    @Autowired
    private IXmsShopifyFulfillmentService shopifyFulfillmentService;
    @Autowired
    private IXmsShopifyFulfillmentItemService shopifyFulfillmentItemService;
    @Resource
    private XmsCustomerProductMapper xmsCustomerProductMapper;
    @Resource
    private XmsCustomerSkuStockMapper xmsCustomerSkuStockMapper;
    @Autowired
    private IXmsShopifyLocationService xmsShopifyLocationService;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private ZoneId zoneId = ZoneId.systemDefault();

    public JSONObject cancelOrderByShopifyName(String shopifyName, String orderNo, Long memberId) throws IOException {
        String url = String.format(shopifyConfig.SHOPIFY_URI_PUT_CANCEL_ORDER, shopifyName, orderNo);
        String accessToken = this.xmsShopifyAuthService.getShopifyToken(shopifyName, memberId);
        Map<String, String> params = new HashMap<>();
        params.put("reason", "cancelOrder");
        // reason
        return this.shopifyRestTemplate.postMap(url, accessToken, params);
    }

    public void getSingleOrder(String shopifyName, String orderNo, Long memberId) {
        String url = String.format(shopifyConfig.SHOPIFY_URI_GET_SINGLE_ORDER, shopifyName, orderNo);
        String accessToken = this.xmsShopifyAuthService.getShopifyToken(shopifyName, memberId);

        String json = this.shopifyRestTemplate.exchange(url, accessToken);
        Orders order = JSONObject.parseObject(JSONObject.parseObject(json).getString("order"), Orders.class);
        OrdersWraper ordersWraper = new OrdersWraper();
        List<Orders> orders = new ArrayList<>();
        orders.add(order);
        ordersWraper.setOrders(orders);
        this.genShopifyOrderInfo(shopifyName, ordersWraper, memberId);
    }

    public int getCountryByShopifyName(String shopifyName, Long memberId) {
        String accessToken = this.xmsShopifyAuthService.getShopifyToken(shopifyName, memberId);
        String url = String.format(shopifyConfig.SHOPIFY_URI_COUNTRIES_LIST, shopifyName);
        String json = this.shopifyRestTemplate.exchange(url, accessToken);
        int total = 0;
        if (null != json) {
            JSONArray jsonArray = JSONObject.parseObject(json).getJSONArray("countries");
            if (null != jsonArray && jsonArray.size() > 0) {
                List<XmsShopifyCountry> list = new ArrayList<>();
                Set<String> set = new HashSet<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    XmsShopifyCountry country = new XmsShopifyCountry();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    country.setCountryId(jsonObject.getString("id"));
                    set.add(jsonObject.getString("id"));
                    country.setName(jsonObject.getString("name"));
                    country.setTax(jsonObject.getString("tax"));
                    country.setCode(jsonObject.getString("code"));
                    country.setTaxName(jsonObject.getString("tax_name"));
                    country.setCreateTime(new Date());
                    country.setProvinces(jsonObject.getString("provinces"));
                    country.setShopifyName(shopifyName);
                    list.add(country);
                }
                QueryWrapper<XmsShopifyCountry> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(XmsShopifyCountry::getShopifyName, shopifyName);
                List<XmsShopifyCountry> hasList = this.xmsShopifyCountryService.list(queryWrapper);
                List<XmsShopifyCountry> insertList = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(hasList)) {
                    hasList.forEach(e -> {
                        if (!set.contains(e.getCountryId())) {
                            insertList.add(e);
                        }
                    });
                } else {
                    insertList.addAll(list);
                }
                total = list.size();
                this.xmsShopifyCountryService.saveBatch(insertList);
                list.clear();
                set.clear();
                hasList.clear();
                insertList.clear();

            }
        }
        return total;
    }

    public int getLocationByShopifyName(String shopifyName, Long memberId) {
        String accessToken = this.xmsShopifyAuthService.getShopifyToken(shopifyName, memberId);
        String url = String.format(shopifyConfig.SHOPIFY_URI_GET_LOCATION, shopifyName);
        String json = this.shopifyRestTemplate.exchange(url, accessToken);
        int total = 0;
        if (null != json) {
            JSONArray jsonArray = JSONObject.parseObject(json).getJSONArray("locations");
            if (null != jsonArray && jsonArray.size() > 0) {
                List<XmsShopifyLocation> list = new ArrayList<>();
                Map<String, XmsShopifyLocation> locationMap = new HashMap<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    XmsShopifyLocation shopifyLocation = new XmsShopifyLocation();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    shopifyLocation.setLocationId(jsonObject.getString("id"));
                    shopifyLocation.setName(jsonObject.getString("name"));
                    shopifyLocation.setLocationJson(jsonObject.toJSONString());
                    shopifyLocation.setMemberId(memberId);
                    shopifyLocation.setCreateTime(new Date());
                    shopifyLocation.setUpdateTime(new Date());
                    shopifyLocation.setShopifyName(shopifyName);
                    locationMap.put(shopifyLocation.getLocationId(), shopifyLocation);
                    list.add(shopifyLocation);
                }
                QueryWrapper<XmsShopifyLocation> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(XmsShopifyLocation::getShopifyName, shopifyName).eq(XmsShopifyLocation::getMemberId, memberId);
                List<XmsShopifyLocation> hasList = this.xmsShopifyLocationService.list(queryWrapper);
                List<XmsShopifyLocation> insertList = new ArrayList<>();
                List<XmsShopifyLocation> updateList = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(hasList)) {
                    hasList.forEach(e -> {
                        if (locationMap.containsKey(e.getLocationId())) {
                            XmsShopifyLocation tempLocal = locationMap.get(e.getLocationId());
                            e.setName(tempLocal.getName());
                            e.setLocationJson(tempLocal.getLocationJson());
                            e.setUpdateTime(new Date());
                            updateList.add(e);
                        } else {
                            insertList.add(e);
                        }
                    });
                } else {
                    insertList.addAll(list);
                }
                total = list.size();
                if (CollectionUtil.isNotEmpty(insertList)) {
                    this.xmsShopifyLocationService.saveBatch(insertList);
                    insertList.clear();
                }
                if (CollectionUtil.isNotEmpty(updateList)) {
                    this.xmsShopifyLocationService.updateBatchById(updateList);
                    updateList.clear();
                }

                list.clear();
                locationMap.clear();
                hasList.clear();
                insertList.clear();

            }
        }
        return total;
    }

    /**
     * 根据shopify的店铺名称获取订单信息
     *
     * @param shopifyName
     * @return
     */
    public Map<String, Set<Long>> getOrdersByShopifyName(String shopifyName, Long memberId) {
        return this.getOrdersSingle(shopifyName, memberId);
    }


    public int getCollectionByShopifyName(String shopName, Long memberId) {

        String accessToken = this.xmsShopifyAuthService.getShopifyToken(shopName, memberId);
        String url = String.format(shopifyConfig.SHOPIFY_URI_CUSTOM_COLLECTIONS, shopName);
        this.getSingleCollection(shopName, url, accessToken, "custom_collections");
        url = String.format(shopifyConfig.SHOPIFY_URI_SMART_COLLECTIONS, shopName);
        this.getSingleCollection(shopName, url, accessToken, "smart_collections");
        return 2;
    }

    private void getSingleCollection(String shopName, String url, String accessToken, String keyName) {

        // custom_collections , smart_collections
        String json = this.shopifyRestTemplate.exchange(url, accessToken);
        JSONObject jsonObject = JSONObject.parseObject(json);

        List<XmsShopifyCollections> list = new ArrayList<>();

        JSONArray jsonArray = jsonObject.getJSONArray(keyName);
        if (null != jsonArray && jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject tempJson = jsonArray.getJSONObject(i);

                XmsShopifyCollections shopifyCollections = new XmsShopifyCollections();
                shopifyCollections.setCollectionsId(tempJson.getLongValue("id"));
                if (tempJson.containsKey("image") && tempJson.getJSONObject("image").containsKey("src")) {
                    shopifyCollections.setImage(tempJson.getJSONObject("image").getString("src"));
                }
                shopifyCollections.setTitle(tempJson.getString("title"));
                shopifyCollections.setShopName(shopName);
                shopifyCollections.setCollectionJson(tempJson.toJSONString());
                shopifyCollections.setCreateTime(new Date());
                shopifyCollections.setCollKey(keyName);
                list.add(shopifyCollections);
            }

            QueryWrapper<XmsShopifyCollections> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsShopifyCollections::getShopName, shopName);
            List<XmsShopifyCollections> hasList = xmsShopifyCollectionsService.list(queryWrapper);
            if (CollectionUtil.isNotEmpty(hasList)) {
                Set<Long> coIdSet = new HashSet<>();
                hasList.forEach(e -> coIdSet.add(e.getCollectionsId()));
                List<XmsShopifyCollections> collect = list.stream().filter(e -> !coIdSet.contains(e.getCollectionsId())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(collect)) {
                    xmsShopifyCollectionsService.saveBatch(collect);
                    collect.clear();
                }
                coIdSet.clear();
                hasList.clear();
            } else {
                xmsShopifyCollectionsService.saveBatch(list);
                list.clear();
            }
        }
    }

    public int getProductsByShopifyName(String shopifyName, Long memberId, String userName) {
        if (StrUtil.isNotEmpty(shopifyName)) {
            String token = this.xmsShopifyAuthService.getShopifyToken(shopifyName, memberId);
            String url = String.format(shopifyConfig.SHOPIFY_URI_PRODUCTS, shopifyName);
            String json = this.shopifyRestTemplate.exchange(url, token);
            JSONObject jsonObject = JSONObject.parseObject(json);
            JSONArray products = jsonObject.getJSONArray("products");
            this.saveShopifyProducts(shopifyName, memberId, userName, products);
        }
        return 0;
    }

    public void saveShopifyProducts(String shopifyName, Long memberId, String userName, JSONArray products) {
        if (null != products && products.size() > 0) {
            int length = products.size();
            for (int i = 0; i < length; i++) {
                this.saveSingleProduct(shopifyName, memberId, userName, products.getJSONObject(i));
            }
        }
    }


    private void saveSingleProduct(String shopifyName, Long memberId, String userName, JSONObject jsonObject) {
        try {
            // 获取sourcing的数据
            //XmsSourcingList sourcingList = this.genXmsSourcingListByShopifyProduct(shopifyName, memberId, userName, jsonObject);
            // 检查并且保存Sourcing数据
            // this.checkXmsSourcingListId(sourcingList);
            // 获取客户的商品信息
            XmsCustomerProduct customerProduct = this.genXmsCustomerProductByShopifyProduct(shopifyName, memberId, userName, jsonObject, 11);
            // 判断是否存在并且保存数据
            boolean b = checkHasCustomerProduct(customerProduct);
            if (!b) {
                QueryWrapper<XmsShopifyPidInfo> pidInfoWrapper = new QueryWrapper<>();
                pidInfoWrapper.lambda().eq(XmsShopifyPidInfo::getShopifyPid, customerProduct.getShopifyProductId());
                List<XmsShopifyPidInfo> pidInfoList = this.xmsShopifyPidInfoMapper.selectList(pidInfoWrapper);
                if (CollectionUtil.isNotEmpty(pidInfoList)) {
                    QueryWrapper<XmsSourcingList> sourcingWrapper = new QueryWrapper<>();
                    sourcingWrapper.lambda().eq(XmsSourcingList::getProductId, Long.parseLong(pidInfoList.get(0).getPid()));
                    List<XmsSourcingList> sourcingLists = this.xmsSourcingListMapper.selectList(sourcingWrapper);
                    if (CollectionUtil.isNotEmpty(sourcingLists)) {
                        XmsSourcingList xmsSourcingList = sourcingLists.get(0);
                        customerProduct.setProductId(xmsSourcingList.getProductId());
                        customerProduct.setSourcingId(xmsSourcingList.getId());
                        xmsSourcingList.setAddProductFlag(1);
                        xmsSourcingList.setUpdateTime(new Date());
                        this.xmsSourcingListMapper.updateById(xmsSourcingList);
                    }
                }
                if (null == customerProduct.getSourcingId()) {
                    customerProduct.setSourcingId(0L);
                }
                if (null == customerProduct.getProductId()) {
                    customerProduct.setProductId(0L);
                }
                this.customerProductService.save(customerProduct);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("saveSingleProduct,shopifyName:[{}],memberId;[{}],product[{}],error:", shopifyName, memberId, jsonObject, e);
        }
    }

    public boolean checkHasCustomerProduct(XmsCustomerProduct customerProduct) {
        QueryWrapper<XmsCustomerProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsCustomerProduct::getShopifyName, customerProduct.getShopifyName())
                .eq(XmsCustomerProduct::getShopifyProductId, customerProduct.getShopifyProductId());
        return this.customerProductService.count(queryWrapper) > 0;
    }

    public XmsCustomerProduct genXmsCustomerProductByShopifyProduct(String shopifyName, Long memberId, String userName, JSONObject shopifyProduct, Integer siteType) {
        XmsCustomerProduct customerProduct = new XmsCustomerProduct();
        customerProduct.setShopifyName(shopifyName);
        customerProduct.setMemberId(memberId);
        customerProduct.setUsername(userName);
        customerProduct.setCreateTime(new Date());
        customerProduct.setUpdateTime(new Date());
        customerProduct.setShopifyJson(shopifyProduct.toJSONString());

        customerProduct.setShopifyProductId(shopifyProduct.getLongValue("id"));
        // customerProduct.setShopifyPrice();
        customerProduct.setSyncTime(new Date());
        customerProduct.setStatus(9);
        customerProduct.setSiteType(siteType);
        customerProduct.setTitle(shopifyProduct.getString("title"));
        if (shopifyProduct.containsKey("image") && null != shopifyProduct.getJSONObject("image") && shopifyProduct.getJSONObject("image").containsKey("src")) {
            customerProduct.setImg(shopifyProduct.getJSONObject("image").getString("src"));
        } else if (shopifyProduct.containsKey("images") && null != shopifyProduct.getJSONArray("images") && shopifyProduct.getJSONArray("images").size() > 0) {
            customerProduct.setImg(shopifyProduct.getJSONArray("images").getJSONObject(0).getString("src"));
        }
        JSONArray variantsArr = shopifyProduct.getJSONArray("variants");
        if (null != variantsArr && variantsArr.size() > 0) {
            double minPrice = 0;
            double maxPrice = 0;
            for (int i = 0; i < variantsArr.size(); i++) {
                double price = variantsArr.getJSONObject(i).getDoubleValue("price");
                if (minPrice == 0 || minPrice > price) {
                    minPrice = price;
                }
                if (maxPrice == 0 || maxPrice < price) {
                    maxPrice = price;
                }
            }
            if (Math.abs(minPrice - maxPrice) < 0.01) {
                customerProduct.setShopifyPrice(BigDecimalUtil.truncateDoubleToString(minPrice, 2));
            } else {
                customerProduct.setShopifyPrice(BigDecimalUtil.truncateDoubleToString(minPrice, 2) + "-" + BigDecimalUtil.truncateDoubleToString(maxPrice, 2));
            }

        }
        return customerProduct;

    }


    /**
     * 更新订单的状态
     *
     * @param orderId
     * @param shopifyName
     * @param statusEnum
     * @return
     */
    public String updateOrder(long orderId, String shopifyName, FulfillmentStatusEnum statusEnum, Long memberId) {
        /**
         * shipped:已发货
         * partial: 部分
         * unshipped:未发货
         * unfulfilled: 返回 状态为 null 或partial的订单
         */
        String url = String.format(shopifyConfig.SHOPIFY_URI_PUT_ORDERS, shopifyName, String.valueOf(orderId));

        Map<String, Object> param = new HashMap<>();
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("id", orderId);
        orderMap.put("fulfillment_status", statusEnum.toString().toLowerCase());
        //orderMap.put("note", statusEnum.toString().toLowerCase());
        param.put("order", orderMap);

        String json = this.shopifyRestTemplate.put(url, this.xmsShopifyAuthService.getShopifyToken(shopifyName, memberId), param);
        return json;
    }

    public String updateFulfillmentOrders(long orderId, String shopifyName, String new_fulfill_at, Long memberId) {
        /**
         * shipped:已发货
         * partial: 部分
         * unshipped:未发货
         * unfulfilled: 返回 状态为 null 或partial的订单
         */
        String url = String.format(shopifyConfig.SHOPIFY_URI_POST_FULFILLMENT_ORDERS, shopifyName, String.valueOf(orderId));

        Map<String, Object> param = new HashMap<>();
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("new_fulfill_at", new_fulfill_at);
        param.put("fulfillment_order", orderMap);

        String json = this.shopifyRestTemplate.post(url, this.xmsShopifyAuthService.getShopifyToken(shopifyName, memberId), param);
        return json;
    }

    /*public String createFulfillmentOrders(List<XmsShopifyOrderDetails> detailsList, FulfillmentParam fulfillmentParam, LogisticsCompanyEnum anElse) {

        String token = this.xmsShopifyAuthService.getShopifyToken(fulfillmentParam.getShopifyName());
        // 步骤1：查询订单以查看其订单项
        // get https://{shop}.myshopify.com/admin/api/2021-04/orders/{order_rest_id}.json

        String url = String.format(shopifyConfig.SHOPIFY_URI_QUERY_ORDERS, fulfillmentParam.getShopifyName(), fulfillmentParam.getOrderNo());
        String json = this.shopifyRestTemplate.exchange(url, token);
        JSONObject orderJson = JSONObject.parseObject(json);
        // 获取variant_id
        JSONArray itemsArray = orderJson.getJSONObject("order").getJSONArray("line_items");

        //步骤2. https://{shop}.myshopify.com/admin/api/2021-04/variants/{variant_rest_id}.json

        String variant_id = itemsArray.getJSONObject(0).getString("variant_id");
        if(StrUtil.isBlank(variant_id)){
            return null;
        }
        url = String.format(shopifyConfig.SHOPIFY_URI_QUERY_VARIANTS, fulfillmentParam.getShopifyName(), variant_id);
        json = this.shopifyRestTemplate.exchange(url, token);

        // 获取inventory_item_ids
        JSONObject variantJson = JSONObject.parseObject(json);
        long inventory_item_id = variantJson.getJSONObject("variant").getLongValue("inventory_item_id");

        // 第3步https://{shop}.myshopify.com/admin/api/2021-04/inventory_levels.json?inventory_item_ids={inventory_item_rest_id}

        url = String.format(shopifyConfig.SHOPIFY_URI_QUERY_INVENTORY_LEVELS, fulfillmentParam.getShopifyName(), inventory_item_id);
        json = this.shopifyRestTemplate.exchange(url, token);
        JSONObject inventoryLevelsJson = JSONObject.parseObject(json);
        JSONArray inventoryLevelsArray = inventoryLevelsJson.getJSONArray("inventory_levels");
        // String location_id = inventoryLevelsArray.getJSONObject(0).getString("inventory_item_id");
        String location_id = inventoryLevelsArray.getJSONObject(0).getString("location_id");

        //第4步post https://{shop}.myshopify.com/admin/api/2021-04/orders/{orders_rest_id}/fulfillments.json


        */

    /**
     * {
     * "fulfillment": {
     * "tracking_url": "http://www.packagetrackr.com/track/somecarrier/1234567",
     * "tracking_company": "Jack Black's Pack, Stack and Track",
     * "line_items": [
     * {
     * "id": 466157049
     * },
     * {
     * "id": 518995019
     * },
     * {
     * "id": 703073504
     * }
     * ]
     * }
     * }
     *//*
        Map<String, Object> param = new HashMap<>();

        Map<String, Object> fulfillmentMap = new HashMap<>();
        fulfillmentMap.put("location_id", location_id);
        fulfillmentMap.put("tracking_number", fulfillmentParam.getTrackingNumber());
        *//*JSONArray jsonArray = new JSONArray();
        jsonArray.add(anElse.getName() + fulfillmentParam.getTrackingNumber());*//*
        fulfillmentMap.put("tracking_url", anElse.getName() + fulfillmentParam.getTrackingNumber());
        //fulfillmentMap.put("tracking_url", anElse.getUrl());

        List<Map<String, Object>> line_itemsList = new ArrayList<>();
        detailsList.forEach(e -> {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", e.getLineItemId());
            line_itemsList.add(itemMap);
        });

        fulfillmentMap.put("line_items", line_itemsList);

        param.put("fulfillment", fulfillmentMap);

        url = String.format(shopifyConfig.SHOPIFY_URI_POST_FULFILLMENT_ORDERS, fulfillmentParam.getShopifyName(), fulfillmentParam.getOrderNo());
        json = this.shopifyRestTemplate.post(url, this.xmsShopifyAuthService.getShopifyToken(fulfillmentParam.getShopifyName()), param);
        return json;

    }*/
    public String createFulfillmentOrders(FulfillmentParam fulfillmentParam, LogisticsCompanyEnum anElse) {

        String token = this.xmsShopifyAuthService.getShopifyToken(fulfillmentParam.getShopifyName(), fulfillmentParam.getMemberId());
        // 步骤1：查询订单以查看其订单项
        // get https://{shop}.myshopify.com/admin/api/2021-04/orders/{order_rest_id}.json

        String url = String.format(shopifyConfig.SHOPIFY_URI_QUERY_ORDERS, fulfillmentParam.getShopifyName(), fulfillmentParam.getOrderNo());
        String json = this.shopifyRestTemplate.exchange(url, token);
        JSONObject orderJson = JSONObject.parseObject(json);
        // 获取variant_id
        JSONArray itemsArray = orderJson.getJSONObject("order").getJSONArray("line_items");

        // 寻找还未设置运单的商品

        List<Map<String, Object>> line_itemsList = new ArrayList<>();
        if (null != itemsArray && itemsArray.size() > 0) {
            for (int i = 0; i < itemsArray.size(); i++) {
                JSONObject tempCl = itemsArray.getJSONObject(i);
                if (StrUtil.isNotBlank(tempCl.getString("fulfillment_status")) && "fulfilled".equalsIgnoreCase(tempCl.getString("fulfillment_status"))) {
                    continue;
                }
                Map<String, Object> tempItem = new HashMap<>();
                tempItem.put("id", tempCl.getLongValue("id"));
                tempItem.put("quantity", tempCl.getLongValue("quantity"));
                line_itemsList.add(tempItem);
            }
        }

        if (line_itemsList.size() == 0) {
            return null;
        }

        //第4步post https://{shop}.myshopify.com/admin/api/2021-04/orders/{orders_rest_id}/fulfillments.json

        /**
         * {
         *     "fulfillment":{
         *         "message":"The package was shipped this morning.",
         *         "notify_customer":false,
         *         "tracking_info":{
         *             "number":1562678,
         *             "url":"https:\/\/www.my-shipping-company.com",
         *             "company":"my-shipping-company"
         *         },
         *         "line_items_by_fulfillment_order":[
         *             {
         *                 "fulfillment_order_id":1046000817,
         *                 "fulfillment_order_line_items":[
         *                     {
         *                         "id":1058737553,
         *                         "quantity":1
         *                     }
         *                 ]
         *             }
         *         ]
         *     }
         * }
         */
        /*
        第一种创建方式
        Map<String, Object> param = new HashMap<>();

        Map<String, Object> fulfillmentMap = new HashMap<>();
        if (StrUtil.isBlank(fulfillmentParam.getMessage())) {
            fulfillmentMap.put("message", "The package was shipped this morning.");
        } else {
            fulfillmentMap.put("message", fulfillmentParam.getMessage());
        }

        fulfillmentMap.put("notify_customer", fulfillmentParam.isNotifyCustomer());

        Map<String, Object> trackingInfoMap = new HashMap<>();
        trackingInfoMap.put("tracking_number", fulfillmentParam.getTrackingNumber());
        if(StrUtil.isBlank(fulfillmentParam.getTrackingCompany())){
            trackingInfoMap.put("tracking_url", anElse.getUrl() + fulfillmentParam.getTrackingNumber());
        } else{
            trackingInfoMap.put("tracking_url", fulfillmentParam.getTrackingCompany());
        }

        if (StrUtil.isBlank(fulfillmentParam.getTrackingCompany())) {
            trackingInfoMap.put("company", "Other Carriers");
        } else {
            trackingInfoMap.put("company", fulfillmentParam.getTrackingCompany());
        }
        fulfillmentMap.put("tracking_info", trackingInfoMap);

        Map<String, Object> line_items_by_fulfillment_order = new HashMap<>();
        line_items_by_fulfillment_order.put("fulfillment_order_id", fulfillmentParam.getOrderNo());
        line_items_by_fulfillment_order.put("fulfillment_order_line_items", line_itemsList);
        List<Map<String, Object>> orderList = new ArrayList<>();
        orderList.add(line_items_by_fulfillment_order);
        fulfillmentMap.put("line_items_by_fulfillment_order", orderList);

        param.put("fulfillment", fulfillmentMap);*/

        // 第二种创建方式

        Map<String, Object> param = new HashMap<>();

        Map<String, Object> fulfillmentMap = new HashMap<>();
        fulfillmentMap.put("location_id", fulfillmentParam.getLocationId());
        fulfillmentMap.put("tracking_number", fulfillmentParam.getTrackingNumber());
        List<String> urls = new ArrayList<>();
        urls.add(anElse.getUrl() + fulfillmentParam.getTrackingNumber());
        fulfillmentMap.put("tracking_urls", urls);
        param.put("fulfillment", fulfillmentMap);
        param.put("notify_customer", fulfillmentParam.isNotifyCustomer());


        //url = String.format(shopifyConfig.SHOPIFY_URI_POST_FULFILLMENT_SERVICE, fulfillmentParam.getShopifyName());
        url = String.format(shopifyConfig.SHOPIFY_URI_POST_FULFILLMENT_ORDERS, fulfillmentParam.getShopifyName(), fulfillmentParam.getOrderNo());
        System.err.println(JSONObject.toJSONString(param));
        json = this.shopifyRestTemplate.post(url, token, param);
        return json;

    }

    public String createFulfillmentOrders2(FulfillmentParam fulfillmentParam, LogisticsCompanyEnum anElse) {

        String token = this.xmsShopifyAuthService.getShopifyToken(fulfillmentParam.getShopifyName(), fulfillmentParam.getMemberId());
        // 步骤1：查询订单以查看其订单项
        // get https://{shop}.myshopify.com/admin/api/2021-04/orders/{order_rest_id}.json

        String url = String.format(shopifyConfig.SHOPIFY_URI_QUERY_ORDERS, fulfillmentParam.getShopifyName(), fulfillmentParam.getOrderNo());
        String json = this.shopifyRestTemplate.exchange(url, token);
        JSONObject orderJson = JSONObject.parseObject(json);
        // 获取variant_id
        JSONArray itemsArray = orderJson.getJSONObject("order").getJSONArray("line_items");

        // 寻找还未设置运单的商品

        List<Map<String, Object>> line_itemsList = new ArrayList<>();
        if (null != itemsArray && itemsArray.size() > 0) {
            for (int i = 0; i < itemsArray.size(); i++) {
                JSONObject tempCl = itemsArray.getJSONObject(i);
                if (StrUtil.isNotBlank(tempCl.getString("fulfillment_status")) && "fulfilled".equalsIgnoreCase(tempCl.getString("fulfillment_status"))) {
                    continue;
                }
                Map<String, Object> tempItem = new HashMap<>();
                tempItem.put("id", tempCl.getLongValue("id"));
                tempItem.put("quantity", tempCl.getLongValue("quantity"));
                line_itemsList.add(tempItem);
            }
        }

        if (line_itemsList.size() == 0) {
            return null;
        }

        //第4步post https://{shop}.myshopify.com/admin/api/2021-04/orders/{orders_rest_id}/fulfillments.json

        /**
         * {
         *     "fulfillment":{
         *         "message":"The package was shipped this morning.",
         *         "notify_customer":false,
         *         "tracking_info":{
         *             "number":1562678,
         *             "url":"https:\/\/www.my-shipping-company.com",
         *             "company":"my-shipping-company"
         *         },
         *         "line_items_by_fulfillment_order":[
         *             {
         *                 "fulfillment_order_id":1046000817,
         *                 "fulfillment_order_line_items":[
         *                     {
         *                         "id":1058737553,
         *                         "quantity":1
         *                     }
         *                 ]
         *             }
         *         ]
         *     }
         * }
         */
        // 第一种创建方式
        Map<String, Object> param = new HashMap<>();

        Map<String, Object> fulfillmentMap = new HashMap<>();
        if (StrUtil.isBlank(fulfillmentParam.getMessage())) {
            fulfillmentMap.put("message", "The package was shipped this morning.");
        } else {
            fulfillmentMap.put("message", fulfillmentParam.getMessage());
        }

        fulfillmentMap.put("notify_customer", fulfillmentParam.isNotifyCustomer());

        Map<String, Object> trackingInfoMap = new HashMap<>();
        trackingInfoMap.put("tracking_number", fulfillmentParam.getTrackingNumber());
        trackingInfoMap.put("tracking_url", anElse.getUrl() + fulfillmentParam.getTrackingNumber());

        if (StrUtil.isBlank(fulfillmentParam.getTrackingCompany())) {
            trackingInfoMap.put("company", "Other Carriers");
        } else {
            trackingInfoMap.put("company", fulfillmentParam.getTrackingCompany());
        }
        fulfillmentMap.put("tracking_info", trackingInfoMap);

        Map<String, Object> line_items_by_fulfillment_order = new HashMap<>();
        line_items_by_fulfillment_order.put("fulfillment_order_id", fulfillmentParam.getOrderNo());
        line_items_by_fulfillment_order.put("fulfillment_order_line_items", line_itemsList);
        List<Map<String, Object>> orderList = new ArrayList<>();
        orderList.add(line_items_by_fulfillment_order);
        fulfillmentMap.put("line_items_by_fulfillment_order", orderList);

        param.put("fulfillment", fulfillmentMap);


        url = String.format(shopifyConfig.SHOPIFY_URI_POST_FULFILLMENT_SERVICE, fulfillmentParam.getShopifyName());

        System.err.println(JSONObject.toJSONString(param));
        json = this.shopifyRestTemplate.post(url, token, param);
        return json;

    }


    public Set<Long> getFulfillmentByShopifyName(String shopifyName, Set<Long> orderNoList, Long memberId) {

        Set<Long> productList = new HashSet<>();
        if (CollectionUtil.isNotEmpty(orderNoList)) {
            String shopifyToken = this.xmsShopifyAuthService.getShopifyToken(shopifyName, memberId);
            orderNoList.forEach(e -> {
                List<XmsShopifyFulfillmentResult> fulfillmentByOrderNoList = this.getFulfillmentByOrderNo(shopifyName, e, shopifyToken);
                if (CollectionUtil.isNotEmpty(fulfillmentByOrderNoList)) {
                    fulfillmentByOrderNoList.forEach(fulfRs -> productList.addAll(this.checkAndSaveFulfillmentResult(fulfRs)));
                }
            });
        }
        return productList;
    }



    private Set<Long> checkAndSaveFulfillmentResult(XmsShopifyFulfillmentResult fulfillment) {

        Set<Long> productList = new HashSet<>();
        try {

            if (null == fulfillment.getFulfillmentId() || fulfillment.getFulfillmentId() == 0) {
                return productList;
            }
            QueryWrapper<XmsShopifyFulfillment> fulfillmentWrapper = new QueryWrapper<>();
            fulfillmentWrapper.lambda().eq(XmsShopifyFulfillment::getShopifyName, fulfillment.getShopifyName())
                    .eq(XmsShopifyFulfillment::getFulfillmentId, fulfillment.getFulfillmentId());
            XmsShopifyFulfillment shopifyFulfillment = this.shopifyFulfillmentService.getOne(fulfillmentWrapper);
            if (null != shopifyFulfillment) {
                shopifyFulfillment.setStatus(fulfillment.getStatus());
                shopifyFulfillment.setService(fulfillment.getService());
                shopifyFulfillment.setUpdatedAt(fulfillment.getUpdatedAt());
                shopifyFulfillment.setUpdateTm(fulfillment.getUpdateTm());
                shopifyFulfillment.setShipmentStatus(fulfillment.getShipmentStatus());
                shopifyFulfillment.setTrackingNumber(fulfillment.getTrackingNumber());
                shopifyFulfillment.setTrackingNumbers(fulfillment.getTrackingNumbers());
                shopifyFulfillment.setTrackingUrl(fulfillment.getTrackingUrl());
                shopifyFulfillment.setTrackingUrls(fulfillment.getTrackingUrls());
                this.shopifyFulfillmentService.updateById(shopifyFulfillment);

                // 更新shopify
                this.shopifyOrderinfoService.setTrackNo(shopifyFulfillment.getOrderId(), fulfillment.getTrackingNumber());
            } else {
                this.shopifyFulfillmentService.save(fulfillment);
                // 更新shopify
                this.shopifyOrderinfoService.setTrackNo(fulfillment.getOrderId(), fulfillment.getTrackingNumber());
            }


            if (CollectionUtil.isNotEmpty(fulfillment.getItemList())) {
                QueryWrapper<XmsShopifyFulfillmentItem> itemQueryWrapper = new QueryWrapper<>();
                itemQueryWrapper.lambda().eq(XmsShopifyFulfillmentItem::getFulfillmentId, fulfillment.getFulfillmentId());
                List<XmsShopifyFulfillmentItem> fulfillmentItemList = this.shopifyFulfillmentItemService.list(itemQueryWrapper);

                if (CollectionUtil.isNotEmpty(fulfillmentItemList)) {
                    Map<String, XmsShopifyFulfillmentItem> itemMap = new HashMap<>();
                    fulfillmentItemList.forEach(e -> itemMap.put(e.getFulfillmentId() + "_" + e.getItemId(), e));
                    fulfillmentItemList.clear();

                    List<XmsShopifyFulfillmentItem> insertList = new ArrayList<>();
                    // List<XmsShopifyFulfillmentItem> updateList = new ArrayList<>();
                    List<Long> dlIds = new ArrayList<>();

                    Set<String> itemSet = new HashSet<>();
                    fulfillment.getItemList().forEach(e -> {
                        productList.add(e.getItemId());
                        itemSet.add(e.getFulfillmentId() + "_" + e.getItemId());
                        if (itemMap.containsKey(e.getFulfillmentId() + "_" + e.getItemId())) {
                            //XmsShopifyFulfillmentItem tempItem = itemMap.get(e.getFulfillmentId());
                            //updateList.add(e);
                        } else {
                            insertList.add(e);
                        }
                    });
                    itemMap.forEach((k, v) -> {
                        if (!itemSet.contains(k)) {
                            dlIds.add(v.getId());
                        }
                    });
                    itemMap.clear();
                        /*if(CollectionUtil.isNotEmpty(updateList)){
                            this.shopifyFulfillmentItemService.saveOrUpdateBatch(updateList);
                            updateList.clear();
                        }*/
                    if (CollectionUtil.isNotEmpty(insertList)) {
                        this.shopifyFulfillmentItemService.saveBatch(insertList);
                        insertList.clear();
                    }
                    if (CollectionUtil.isNotEmpty(dlIds)) {
                        this.shopifyFulfillmentItemService.removeByIds(dlIds);
                        dlIds.clear();
                    }
                } else {
                    this.shopifyFulfillmentItemService.saveBatch(fulfillment.getItemList());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("checkAndSaveFulfillmentResult,fulfillmentByOrderNo[{}],,error:", fulfillment, e);
        }
        return productList;
    }


    private List<XmsShopifyFulfillmentResult> getFulfillmentByOrderNo(String shopifyName, Long orderNo, String token) {

        List<XmsShopifyFulfillmentResult> resultList = new ArrayList<>();
        try {
            // uri_post_fulfillment_orders
            String url = String.format(shopifyConfig.SHOPIFY_URI_POST_FULFILLMENT_ORDERS, shopifyName, String.valueOf(orderNo));
            String rs = this.shopifyRestTemplate.exchange(url, token);
            if (null != rs) {
                JSONArray fulfillments = JSONObject.parseObject(rs).getJSONArray("fulfillments");

                if (null != fulfillments && fulfillments.size() > 0) {
                    for (int k = 0; k < fulfillments.size(); k++) {
                        JSONObject jsonObject = fulfillments.getJSONObject(k);
                        XmsShopifyFulfillmentResult fulfillment = new XmsShopifyFulfillmentResult();
                        fulfillment.setShopifyName(shopifyName);
                        fulfillment.setFulfillmentId(jsonObject.getLong("id"));
                        fulfillment.setOrderId(jsonObject.getLong("order_id"));
                        fulfillment.setStatus(jsonObject.getString("status"));
                        fulfillment.setCreatedAt(jsonObject.getString("created_at"));
                        fulfillment.setCreateTime(new Date());
                        if (StrUtil.isNotBlank(fulfillment.getCreatedAt())) {
                            LocalDateTime localDate = LocalDateTime.parse(fulfillment.getCreatedAt().replace("T", " ").substring(0, 19), dateTimeFormatter);
                            ZonedDateTime zdt = localDate.atZone(zoneId);
                            fulfillment.setCreateTm(Date.from(zdt.toInstant()));
                        }
                        fulfillment.setService(jsonObject.getString("service"));
                        fulfillment.setUpdatedAt(jsonObject.getString("updated_at"));
                        if (StrUtil.isNotBlank(fulfillment.getUpdatedAt())) {
                            LocalDateTime localDate = LocalDateTime.parse(fulfillment.getUpdatedAt().replace("T", " ").substring(0, 19), dateTimeFormatter);
                            ZonedDateTime zdt = localDate.atZone(zoneId);
                            fulfillment.setUpdateTm(Date.from(zdt.toInstant()));
                        }
                        fulfillment.setTrackingCompany(jsonObject.getString("tracking_company"));
                        fulfillment.setShipmentStatus(jsonObject.getString("shipment_status"));
                        fulfillment.setLocationId(jsonObject.getString("location_id"));
                        fulfillment.setTrackingNumber(jsonObject.getString("tracking_number"));
                        JSONArray tracking_numbers = jsonObject.getJSONArray("tracking_numbers");
                        if (null != tracking_numbers) {
                            fulfillment.setTrackingNumbers(tracking_numbers.toJSONString());
                        }
                        fulfillment.setTrackingUrl(jsonObject.getString("tracking_url"));
                        JSONArray tracking_urls = jsonObject.getJSONArray("tracking_urls");
                        if (null != tracking_urls) {
                            fulfillment.setTrackingUrls(tracking_urls.toJSONString());
                        }
                        fulfillment.setReceipt(jsonObject.getString("receipt"));
                        fulfillment.setName(jsonObject.getString("name"));
                        fulfillment.setAdminGraphqlApiId(jsonObject.getString("admin_graphql_api_id"));

                        List<XmsShopifyFulfillmentItem> itemList = new ArrayList<>();

                        JSONArray line_items = jsonObject.getJSONArray("line_items");
                        if (null != line_items && line_items.size() > 0) {
                            for (int i = 0; i < line_items.size(); i++) {
                                JSONObject itemsJson = line_items.getJSONObject(i);
                                XmsShopifyFulfillmentItem fulfillmentItem = new XmsShopifyFulfillmentItem();
                                fulfillmentItem.setOrderId(orderNo);
                                fulfillmentItem.setShopifyName(shopifyName);
                                fulfillmentItem.setFulfillmentId(fulfillment.getFulfillmentId());
                                fulfillmentItem.setItemId(itemsJson.getLong("id"));
                                fulfillmentItem.setVariantId(itemsJson.getString("variant_id"));
                                fulfillmentItem.setTitle(itemsJson.getString("title"));
                                fulfillmentItem.setQuantity(itemsJson.getInteger("quantity"));
                                fulfillmentItem.setSku(itemsJson.getString("sku"));
                                fulfillmentItem.setVariantTitle(itemsJson.getString("variant_title"));
                                fulfillmentItem.setVendor(itemsJson.getString("vendor"));
                                fulfillmentItem.setFulfillmentService(itemsJson.getString("fulfillment_service"));
                                fulfillmentItem.setProductId(itemsJson.getLongValue("product_id"));
                                fulfillmentItem.setRequiresShipping(itemsJson.getBoolean("requires_shipping") ? 1 : 0);
                                fulfillmentItem.setTaxable(itemsJson.getBoolean("taxable") ? 1 : 0);
                                fulfillmentItem.setGiftCard(itemsJson.getBoolean("gift_card") ? 1 : 0);
                                fulfillmentItem.setName(itemsJson.getString("name"));
                                fulfillmentItem.setVariantInventoryManagement(itemsJson.getString("variant_inventory_management"));
                                fulfillmentItem.setProperties(itemsJson.getString("properties"));
                                fulfillmentItem.setProductExists(itemsJson.getInteger("product_exists"));
                                fulfillmentItem.setFulfillableQuantity(itemsJson.getInteger("fulfillable_quantity"));
                                fulfillmentItem.setGrams(itemsJson.getInteger("grams"));
                                fulfillmentItem.setPrice(itemsJson.getString("price"));
                                fulfillmentItem.setTotalDiscount(itemsJson.getString("total_discount"));
                                fulfillmentItem.setFulfillmentStatus(itemsJson.getString("fulfillment_status"));
                                fulfillmentItem.setPriceSet(itemsJson.getString("price_set"));
                                fulfillmentItem.setTotalDiscountSet(itemsJson.getString("total_discount_set"));
                                fulfillmentItem.setDiscountAllocations(itemsJson.getString("discount_allocations"));
                                fulfillmentItem.setAdminGraphqlApiId(itemsJson.getString("admin_graphql_api_id"));
                                fulfillmentItem.setTaxLines(itemsJson.getString("tax_lines"));
                                fulfillmentItem.setCreateTime(new Date());
                                itemList.add(fulfillmentItem);
                            }
                        }
                        fulfillment.setItemList(itemList);
                        resultList.add(fulfillment);
                    }

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getFulfillmentByOrderNo,shopifyName[{}],orderNo[{}],error:", shopifyName, orderNo, e);
        }
        return resultList;
    }


    public String deleteProduct(String[] idsList, String shopifyName, Long memberId) {

        QueryWrapper<XmsCustomerProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(XmsCustomerProduct::getId, Arrays.asList(idsList));
        List<XmsCustomerProduct> productList = this.customerProductService.list(queryWrapper);
        if (CollectionUtil.isNotEmpty(productList)) {

            String shopifyToken = this.xmsShopifyAuthService.getShopifyToken(shopifyName, memberId);

            List<Long> idList = new ArrayList<>();

            Set<Long> pidSet = new HashSet<>();
            // 删除商品
            for (XmsCustomerProduct xmsCustomerProduct : productList) {
                if (pidSet.contains(xmsCustomerProduct.getShopifyProductId())) {
                    idList.add(xmsCustomerProduct.getId());
                } else {
                    boolean b = this.singleDeleteProduct(xmsCustomerProduct.getShopifyName(), xmsCustomerProduct.getShopifyProductId(), shopifyToken);
                    if (b) {
                        pidSet.add(xmsCustomerProduct.getShopifyProductId());
                        idList.add(xmsCustomerProduct.getId());
                    }
                }
            }

            pidSet.clear();


            List<Long> shopifyPidList = new ArrayList<>();
            // 设置sourcingList的标识
            List<Long> collect = productList.stream().mapToLong(XmsCustomerProduct::getSourcingId).boxed().collect(Collectors.toList());
            productList.forEach(e -> shopifyPidList.add(e.getShopifyProductId()));

            UpdateWrapper<XmsSourcingList> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().in(XmsSourcingList::getId, collect).set(XmsSourcingList::getAddProductFlag, 0);
            this.xmsSourcingListMapper.update(null, updateWrapper);
            collect.clear();

            if (CollectionUtil.isNotEmpty(idList)) {
                // 移除商品
                this.xmsCustomerProductMapper.deleteBatchIds(idList);
            }

            /*// 移除库存
            UpdateWrapper<XmsCustomerSkuStock> deleteWrapper = new UpdateWrapper<>();
            deleteWrapper.lambda().in(XmsCustomerSkuStock::getProductId, idList);
            this.xmsCustomerSkuStockMapper.delete(deleteWrapper);*/

            if (CollectionUtil.isNotEmpty(shopifyPidList)) {

                QueryWrapper<XmsShopifyPidInfo> pidInfoQueryWrapper = new QueryWrapper<>();
                // 删除关联关系
                pidInfoQueryWrapper.lambda().eq(XmsShopifyPidInfo::getShopifyName, shopifyName)
                        .in(XmsShopifyPidInfo::getShopifyPid, shopifyPidList);
                this.xmsShopifyPidInfoMapper.delete(pidInfoQueryWrapper);
                shopifyPidList.clear();
            }


            idList.clear();

            productList.clear();
            return "success size:" + idList.size();
        }
        return null;
    }

    public int setTrackNo(Long orderNo, String trackNo) {
        return this.shopifyOrderinfoService.setTrackNo(orderNo, trackNo);
    }


    private boolean singleDeleteProduct(String shopifyName, Long shopifyPid, String shopifyToken) {
        try {
            String url = String.format(shopifyConfig.SHOPIFY_URI_DELETE_PRODUCTS, shopifyName, shopifyPid);
            String delete = this.shopifyRestTemplate.delete(url, shopifyToken);
            System.err.println("," + shopifyPid + ":" + delete);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 根据店铺获取订单数据
     *
     * @param shopName
     * @return
     */
    private OrdersWraper getOrders(String shopName, Long memberId) {
        String url = String.format(shopifyConfig.SHOPIFY_URI_ORDERS, shopName);
        String accessToken = this.xmsShopifyAuthService.getShopifyToken(shopName, memberId);
        String json = this.shopifyRestTemplate.exchange(url, accessToken);
        OrdersWraper result = new Gson().fromJson(json, OrdersWraper.class);
        return result;
    }

    /**
     * 获取shopify的订单
     *
     * @param orderNo
     * @return
     */
    public List<XmsShopifyOrderinfo> queryListByOrderNo(Long orderNo) {
        return this.shopifyOrderinfoService.queryListByOrderNo(orderNo);
    }

    public List<XmsShopifyOrderDetails> queryDetailsListByOrderNo(Long orderNo) {
        QueryWrapper<XmsShopifyOrderDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsShopifyOrderDetails::getOrderNo, orderNo);
        return this.shopifyOrderDetailsService.list(queryWrapper);
    }

    /**
     * 根据数据生成订单数据
     *
     * @param shopifyName
     * @param orders
     */
    private Set<Long> genShopifyOrderInfo(String shopifyName, OrdersWraper orders, Long memberId) {
        Set<Long> productList = new HashSet<>();


        List<Orders> shopifyOrderList = orders.getOrders();

        List<XmsShopifyOrderinfo> existList = this.shopifyOrderinfoService.queryListByShopifyName(shopifyName);

        List<Orders> insertList;
        List<XmsShopifyOrderinfo> updateList = new ArrayList<>();
        Map<Long, Orders> itemsMap = new HashMap<>();

        if (CollectionUtil.isNotEmpty(existList)) {
            // String nowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // 过滤已经存在的订单
            Map<Long, XmsShopifyOrderinfo> idSet = new HashMap<>(existList.size() * 2);
            existList.forEach(e -> idSet.put(e.getOrderNo(), e));
            insertList = shopifyOrderList.stream().filter(e -> !idSet.containsKey(e.getId())).collect(Collectors.toList());

            // 处理订单更新
            shopifyOrderList.forEach(e -> {
                if (idSet.containsKey(e.getId())) {
                    XmsShopifyOrderinfo tempOrder = idSet.get(e.getId());
                    tempOrder.setTotalPriceUsd(e.getTotal_price_usd());
                    tempOrder.setFinancialStatus(e.getFinancial_status());
                    tempOrder.setFulfillmentStatus(e.getFulfillment_status());
                    tempOrder.setUpdatedAt(e.getUpdated_at());
                    tempOrder.setUpdateTime(new Date());
                    tempOrder.setCancelledAt(e.getCancelled_at());
                    tempOrder.setCancelReason(e.getCancel_reason());
                    updateList.add(tempOrder);
                    itemsMap.put(e.getId(), e);
                }
            });
            if (CollectionUtil.isNotEmpty(updateList)) {
                for (XmsShopifyOrderinfo orderInfo : updateList) {
                    try {
                        orderInfo.setMemberId(memberId);
                        this.shopifyOrderinfoService.saveOrUpdate(orderInfo);
                        if (itemsMap.containsKey(orderInfo.getOrderNo())) {
                            productList.addAll(this.dealDetailsAndAddress(itemsMap.get(orderInfo.getOrderNo())));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("shopifyName:" + shopifyName + ",genShopifyOrderInfo error:", e);
                    }

                }
                itemsMap.clear();
                updateList.clear();
            }
            idSet.clear();
        } else {
            insertList = new ArrayList<>(shopifyOrderList);
        }
        if (CollectionUtil.isNotEmpty(insertList)) {

            for (Orders orderInfo : insertList) {
                try {
                    orderInfo.setShopify_name(shopifyName);
                    // shopifyOrderMapper.insertOrderInfoSingle(orderInfo);
                    XmsShopifyOrderinfo xmsShopifyOrderinfo = this.genXmsShopifyOrderinfo(orderInfo);
                    xmsShopifyOrderinfo.setMemberId(memberId);
                    this.shopifyOrderinfoService.save(xmsShopifyOrderinfo);

                    productList.addAll(this.dealDetailsAndAddress(orderInfo));
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("shopifyName:" + shopifyName + ",genShopifyOrderInfo error:", e);
                }
            }
        }
        shopifyOrderList.clear();
        return productList;
    }

    private Map<String, Set<Long>> getOrdersSingle(String shopifyName, Long memberId) {
        Map<String, Set<Long>> rsMap = new HashMap<>();
        Set<Long> pidList = new HashSet<>();
        Set<Long> orderList = new HashSet<>();
        if (StrUtil.isBlank(shopifyName)) {
            return rsMap;
        }
        try {

            OrdersWraper orders = this.getOrders(shopifyName, memberId);
            if (null != orders && CollectionUtil.isNotEmpty(orders.getOrders())) {
                orders.getOrders().stream().filter(e -> CollectionUtil.isNotEmpty(e.getLine_items())).forEach(e -> e.getLine_items().forEach(el -> pidList.add(el.getProduct_id())));

                orders.getOrders().forEach(e-> orderList.add(e.getId()) );

                // 执行插入数据
                this.genShopifyOrderInfo(shopifyName, orders, memberId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getOrdersSingle,shopifyName[{}],error:", shopifyName, e);
        }
        rsMap.put("pidList", pidList);
        rsMap.put("orderList", orderList);
        return rsMap;
    }

    private String getShopifyProductUrl(String shopifyName, Long productId) {
        // https://sunsharetts.myshopify.com/admin/products/6719946850481
        return "https://" + shopifyName + ".myshopify.com/admin/products/" + productId;
    }


    public void checkXmsSourcingListId(XmsSourcingList sourcingList) {
        QueryWrapper<XmsSourcingList> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XmsSourcingList::getMemberId, sourcingList.getMemberId())
                .eq(XmsSourcingList::getSiteType, 11)
                .eq(XmsSourcingList::getUrl, sourcingList.getUrl());
        XmsSourcingList one = this.sourcingListService.getOne(queryWrapper);
        if (null == one || null == one.getId() || one.getId() == 0) {
            this.sourcingListService.save(sourcingList);
        } else {
            sourcingList.setId(one.getId());
        }
    }

    public XmsSourcingList genXmsSourcingListByShopifyProduct(String shopifyName, Long memberId, String
            userName, JSONObject shopifyProduct) {
        XmsSourcingList sourcingList = new XmsSourcingList();
        sourcingList.setMemberId(memberId);
        sourcingList.setUsername(userName);
        sourcingList.setCreateTime(new Date());
        sourcingList.setUpdateTime(new Date());
        sourcingList.setStatus(0);
        sourcingList.setSiteType(11);
        sourcingList.setRemark("shopify product");
        sourcingList.setTitle(shopifyProduct.getString("title"));
        if (shopifyProduct.containsKey("images") && null != shopifyProduct.getJSONArray("images")) {
            sourcingList.setImages(shopifyProduct.getJSONArray("images").getJSONObject(0).getString("src"));
        }
        sourcingList.setUrl(this.getShopifyProductUrl(shopifyName, shopifyProduct.getLongValue("id")));
        return sourcingList;

    }

    public void updateShopifyOrder(XmsShopifyOrderinfo xmsShopifyOrderinfo) {
        UpdateWrapper<XmsShopifyOrderinfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(XmsShopifyOrderinfo::getOrderNo, xmsShopifyOrderinfo.getOrderNo())
                .set(XmsShopifyOrderinfo::getFulfillmentServiceId, xmsShopifyOrderinfo.getFulfillmentServiceId())
                .set(XmsShopifyOrderinfo::getLocationId, xmsShopifyOrderinfo.getLocationId());
        this.shopifyOrderinfoService.update(null, updateWrapper);
    }

    private Set<Long> dealDetailsAndAddress(Orders orderInfo) {
        Set<Long> productList = new HashSet<>();

        if (CollectionUtil.isNotEmpty(orderInfo.getLine_items())) {
            // 删除原来数据
            this.shopifyOrderDetailsService.deleteByOrderNo(orderInfo.getId());
            for (Line_items item : orderInfo.getLine_items()) {
                item.setOrder_no(orderInfo.getId());
                // shopifyOrderMapper.insertOrderDetails(item);
                productList.add(item.getProduct_id());

                XmsShopifyOrderDetails xmsShopifyOrderDetails = this.genXmsShopifyOrderDetails(item);
                this.shopifyOrderDetailsService.save(xmsShopifyOrderDetails);
            }

            // 读取商品的图片信息
            // this.dealShopifyProductImg(productList, accessToken, shopifyName);
        }
        if (orderInfo.getShipping_address() != null) {
            // 删除原来数据
            //shopifyOrderMapper.deleteOrderAddress(orderInfo.getId());
            this.shopifyOrderAddressService.deleteByOrderNo(orderInfo.getId());

            orderInfo.getShipping_address().setOrder_no(orderInfo.getId());

            // shopifyOrderMapper.insertIntoOrderAddress(orderInfo.getShipping_address());
            XmsShopifyOrderAddress xmsShopifyOrderAddress = this.genXmsShopifyOrderAddress(orderInfo.getShipping_address());
            this.shopifyOrderAddressService.save(xmsShopifyOrderAddress);
        }
        return productList;
    }


    /**
     * shopify过来的数据转换成可存储数据
     *
     * @param orderInfo
     * @return
     */
    private XmsShopifyOrderinfo genXmsShopifyOrderinfo(Orders orderInfo) {
        /*单个插入订单表*/
        String SINGLE_ORDERINFO_INSERT = "insert into shopify_orderinfo(id,shopify_name,email,closed_at,created_at,updated_at,number,note,token,gateway,test,total_price,subtotal_price,total_weight,total_tax,taxes_included,currency,financial_status,confirmed,total_discounts,total_line_items_price,cart_token,buyer_accepts_marketing,name,referring_site,landing_site,cancelled_at,cancel_reason,total_price_usd,checkout_token,reference,user_id,location_id,source_identifier,source_url,processed_at,device_id,phone,customer_locale,app_id,browser_ip,landing_site_ref,order_number,processing_method,checkout_id,source_name,fulfillment_status,tags,contact_email,order_status_url,presentment_currency,admin_graphql_api_id) value (#{id},#{shopify_name},#{email},#{closed_at},#{created_at},#{updated_at},#{number},#{note},#{token},#{gateway},#{test},#{total_price},#{subtotal_price},#{total_weight},#{total_tax},#{taxes_included},#{currency},#{financial_status},#{confirmed},#{total_discounts},#{total_line_items_price},#{cart_token},#{buyer_accepts_marketing},#{name},#{referring_site},#{landing_site},#{cancelled_at},#{cancel_reason},#{total_price_usd},#{checkout_token},#{reference},#{user_id},#{location_id},#{source_identifier},#{source_url},#{processed_at},#{device_id},#{phone},#{customer_locale},#{app_id},#{browser_ip},#{landing_site_ref},#{order_number},#{processing_method},#{checkout_id},#{source_name},#{fulfillment_status},#{tags},#{contact_email},#{order_status_url},#{presentment_currency},#{admin_graphql_api_id})";
        XmsShopifyOrderinfo shopifyOrderinfo = new XmsShopifyOrderinfo();

        BeanUtil.copyProperties(orderInfo, shopifyOrderinfo);

        shopifyOrderinfo.setId(null);
        shopifyOrderinfo.setOrderNo(orderInfo.getId());
        shopifyOrderinfo.setShopifyName(orderInfo.getShopify_name());
        shopifyOrderinfo.setClosedAt(orderInfo.getClosed_at());
        if (StrUtil.isNotBlank(orderInfo.getCreated_at())) {
            // 2021-08-13T01:55:49
            shopifyOrderinfo.setCreatedAt(orderInfo.getCreated_at().trim().substring(0, 19).replace("T", " "));
        }
        shopifyOrderinfo.setUpdatedAt(orderInfo.getUpdated_at());
        shopifyOrderinfo.setTotalPrice(orderInfo.getTotal_price());
        shopifyOrderinfo.setSubtotalPrice(orderInfo.getSubtotal_price());
        shopifyOrderinfo.setTotalWeight(orderInfo.getTotal_weight());
        shopifyOrderinfo.setTotalTax(orderInfo.getTotal_tax());
        shopifyOrderinfo.setTaxesIncluded(orderInfo.getTaxes_included() ? 1 : 0);
        shopifyOrderinfo.setFinancialStatus(orderInfo.getFinancial_status());
        shopifyOrderinfo.setTotalDiscounts(orderInfo.getTotal_discounts());
        shopifyOrderinfo.setTotalLineItemsPrice(orderInfo.getTotal_line_items_price());
        shopifyOrderinfo.setCartToken(orderInfo.getCart_token());
        shopifyOrderinfo.setBuyerAcceptsMarketing(orderInfo.getBuyer_accepts_marketing() ? 1 : 0);
        shopifyOrderinfo.setReferringSite(orderInfo.getReferring_site());
        shopifyOrderinfo.setLandingSite(orderInfo.getLanding_site());
        shopifyOrderinfo.setCancelledAt(orderInfo.getCancelled_at());
        shopifyOrderinfo.setCancelReason(orderInfo.getCancel_reason());
        shopifyOrderinfo.setTotalPriceUsd(orderInfo.getTotal_price_usd());
        shopifyOrderinfo.setCheckoutToken(orderInfo.getCheckout_token());
        shopifyOrderinfo.setUserId(orderInfo.getUser_id());
        shopifyOrderinfo.setLocationId(orderInfo.getLocation_id());
        shopifyOrderinfo.setSourceIdentifier(orderInfo.getSource_identifier());
        shopifyOrderinfo.setSourceUrl(orderInfo.getSource_url());
        shopifyOrderinfo.setProcessedAt(orderInfo.getProcessed_at());
        shopifyOrderinfo.setDeviceId(orderInfo.getDevice_id());
        shopifyOrderinfo.setCustomerLocale(orderInfo.getCustomer_locale());
        shopifyOrderinfo.setAppId(orderInfo.getApp_id());
        shopifyOrderinfo.setBrowserIp(orderInfo.getBrowser_ip());
        shopifyOrderinfo.setLandingSiteRef(orderInfo.getLanding_site_ref());
        shopifyOrderinfo.setOrderNumber(orderInfo.getOrder_number());
        shopifyOrderinfo.setProcessingMethod(orderInfo.getProcessing_method());
        shopifyOrderinfo.setCheckoutId(orderInfo.getCheckout_id());
        shopifyOrderinfo.setSourceName(orderInfo.getSource_name());
        shopifyOrderinfo.setFulfillmentStatus(orderInfo.getFulfillment_status());
        shopifyOrderinfo.setContactEmail(orderInfo.getContact_email());
        shopifyOrderinfo.setOrderStatusUrl(orderInfo.getOrder_status_url());
        shopifyOrderinfo.setPresentmentCurrency(orderInfo.getPresentment_currency());
        shopifyOrderinfo.setAdminGraphqlApiId(orderInfo.getAdmin_graphql_api_id());
        shopifyOrderinfo.setCreateTime(new Date());
        shopifyOrderinfo.setUpdateTime(new Date());
        shopifyOrderinfo.setLocationId(orderInfo.getLocation_id());
        if (null != orderInfo.getTotal_shipping_price_set() && null != orderInfo.getTotal_shipping_price_set().getShop_money()) {
            shopifyOrderinfo.setTotalShippingPrice(orderInfo.getTotal_shipping_price_set().getShop_money().getAmount());
        } else {
            shopifyOrderinfo.setTotalShippingPrice("0");
        }

        return shopifyOrderinfo;
    }

    /**
     * Line_items 转换成可存储的数据
     *
     * @param item
     * @return
     */
    private XmsShopifyOrderDetails genXmsShopifyOrderDetails(Line_items item) {

        String SINGLE_ORDER_DETAILS_INSERT = "insert into shopify_order_details(order_no,variant_id,title,quantity,sku,variant_title,vendor,fulfillment_service,product_id,requires_shipping,taxable,gift_card,name,variant_inventory_management,product_exists,fulfillable_quantity,grams,price,total_discount,fulfillment_status,admin_graphql_api_id) values(#{order_no},#{variant_id},#{title},#{quantity},#{sku},#{variant_title},#{vendor},#{fulfillment_service},#{product_id},#{requires_shipping},#{taxable},#{gift_card},#{name},#{variant_inventory_management},#{product_exists},#{fulfillable_quantity},#{grams},#{price},#{total_discount},#{fulfillment_status},#{admin_graphql_api_id})";

        XmsShopifyOrderDetails orderDetails = new XmsShopifyOrderDetails();

        BeanUtil.copyProperties(item, orderDetails);
        orderDetails.setOrderNo(item.getOrder_no());
        orderDetails.setVariantId(item.getVariant_id());
        orderDetails.setVariantTitle(item.getVariant_title());
        orderDetails.setFulfillmentService(item.getFulfillment_service());
        orderDetails.setProductId(item.getProduct_id());
        orderDetails.setRequiresShipping(item.getRequires_shipping() ? 1 : 0);
        orderDetails.setGiftCard(item.getGift_card() ? 1 : 0);
        orderDetails.setVariantInventoryManagement(item.getVariant_inventory_management());
        orderDetails.setProductExists(item.getProduct_exists() ? 1 : 0);
        orderDetails.setFulfillableQuantity((long) item.getFulfillable_quantity());
        orderDetails.setTotalDiscount(item.getTotal_discount());
        orderDetails.setFulfillmentStatus(item.getFulfillment_status());
        orderDetails.setAdminGraphqlApiId(item.getAdmin_graphql_api_id());
        orderDetails.setCreateTime(new Date());
        orderDetails.setLineItemId(item.getId());

        return orderDetails;
    }

    /**
     * shipping_address 转换成可存储的数据
     *
     * @param shipping_address
     * @return
     */
    private XmsShopifyOrderAddress genXmsShopifyOrderAddress(Shipping_address shipping_address) {
        String SINGLE_ORDER_ADDRESS_INSERT = "insert into shopify_order_address(order_no,first_name,address1,phone,city,zip,province,country,last_name,address2,company,latitude,longitude,name,country_code,province_code) values(#{order_no},#{first_name},#{address1},#{phone},#{city},#{zip},#{province},#{country},#{last_name},#{address2},#{company},#{latitude},#{longitude},#{name},#{country_code},#{province_code})";


        XmsShopifyOrderAddress orderAddress = new XmsShopifyOrderAddress();
        BeanUtil.copyProperties(shipping_address, orderAddress);

        orderAddress.setOrderNo(shipping_address.getOrder_no());
        orderAddress.setFirstName(shipping_address.getFirst_name());
        orderAddress.setLastName(shipping_address.getLast_name());
        orderAddress.setCountryCode(shipping_address.getCountry_code());
        orderAddress.setProvinceCode(shipping_address.getProvince_code());
        orderAddress.setCreateTime(new Date());

        return orderAddress;
    }

}
