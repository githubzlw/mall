package com.macro.mall.shopify.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.Gson;
import com.macro.mall.entity.XmsShopifyOrderAddress;
import com.macro.mall.entity.XmsShopifyOrderDetails;
import com.macro.mall.entity.XmsShopifyOrderinfo;
import com.macro.mall.shopify.config.ShopifyConfig;
import com.macro.mall.shopify.config.ShopifyRestTemplate;
import com.macro.mall.shopify.pojo.FulfillmentParam;
import com.macro.mall.shopify.pojo.FulfillmentStatusEnum;
import com.macro.mall.shopify.pojo.LogisticsCompanyEnum;
import com.macro.mall.shopify.pojo.orders.Line_items;
import com.macro.mall.shopify.pojo.orders.Orders;
import com.macro.mall.shopify.pojo.orders.OrdersWraper;
import com.macro.mall.shopify.pojo.orders.Shipping_address;
import com.macro.mall.shopify.service.IXmsShopifyAuthService;
import com.macro.mall.shopify.service.IXmsShopifyOrderAddressService;
import com.macro.mall.shopify.service.IXmsShopifyOrderDetailsService;
import com.macro.mall.shopify.service.IXmsShopifyOrderinfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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


    /**
     * 根据shopify的店铺名称获取订单信息
     *
     * @param shopifyNameList
     * @return
     */
    public int getOrdersByShopifyName(List<String> shopifyNameList) {

        AtomicInteger total = new AtomicInteger();
        if (CollectionUtil.isNotEmpty(shopifyNameList)) {
            shopifyNameList.forEach(e -> total.addAndGet(this.getOrdersSingle(e)));
        }
        return total.get();
    }


    public void updateShopifyOrder(XmsShopifyOrderinfo xmsShopifyOrderinfo) {
        UpdateWrapper<XmsShopifyOrderinfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(XmsShopifyOrderinfo::getOrderNo, xmsShopifyOrderinfo.getOrderNo())
                .set(XmsShopifyOrderinfo::getFulfillmentServiceId, xmsShopifyOrderinfo.getFulfillmentServiceId())
                .set(XmsShopifyOrderinfo::getLocationId, xmsShopifyOrderinfo.getLocationId());
        this.shopifyOrderinfoService.update(null, updateWrapper);
    }

    /**
     * 更新订单的状态
     *
     * @param orderId
     * @param shopifyName
     * @param statusEnum
     * @return
     */
    public String updateOrder(long orderId, String shopifyName, FulfillmentStatusEnum statusEnum) {
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

        String json = this.shopifyRestTemplate.put(url, this.xmsShopifyAuthService.getShopifyToken(shopifyName), param);
        return json;
    }


    public String updateFulfillmentOrders(long orderId, String shopifyName, String new_fulfill_at) {
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

        String json = this.shopifyRestTemplate.post(url, this.xmsShopifyAuthService.getShopifyToken(shopifyName), param);
        return json;
    }


    public String createFulfillmentOrders(XmsShopifyOrderinfo shopifyOrderinfo, List<XmsShopifyOrderDetails> detailsList, FulfillmentParam fulfillmentParam, LogisticsCompanyEnum anElse) {

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

        /**
         * {
         *   "fulfillment": {
         *     "location_id": "{location_rest_id}",
         *     "tracking_number": "{tracking_number}",
         *     "line_items": [
         *       {
         *         "id": "{line_item_rest_id}"
         *       }
         *     ]
         *   }
         * }
         */
        Map<String, Object> param = new HashMap<>();

        Map<String, Object> fulfillmentMap = new HashMap<>();
        fulfillmentMap.put("location_id", location_id);
        fulfillmentMap.put("tracking_number", fulfillmentParam.getTrackingNumber());
        fulfillmentMap.put("tracking_company", anElse.getName());
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

    }







    private int getOrdersSingle(String shopifyName) {
        try {

            OrdersWraper orders = this.getOrders(shopifyName);
            if (null != orders && CollectionUtil.isNotEmpty(orders.getOrders())) {
                // 执行插入数据
                this.genShopifyOrderInfo(shopifyName, orders);
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getOrdersSingle,shopifyName[{}],error:", shopifyName, e);
            return 0;
        }
    }

    /**
     * 根据店铺获取订单数据
     *
     * @param shopName
     * @return
     */
    public OrdersWraper getOrders(String shopName) {
        String url = String.format(shopifyConfig.SHOPIFY_URI_ORDERS, shopName);
        String accessToken = this.xmsShopifyAuthService.getShopifyToken(shopName);
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
    public void genShopifyOrderInfo(String shopifyName, OrdersWraper orders) {
        List<Orders> shopifyOrderList = orders.getOrders();

        List<XmsShopifyOrderinfo> existList = this.shopifyOrderinfoService.queryListByShopifyName(shopifyName);

        List<Orders> insertList = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(existList)) {
            // 过滤已经存在的订单
            Map<Long, XmsShopifyOrderinfo> idSet = new HashMap<>(existList.size() * 2);
            existList.forEach(e -> idSet.put(e.getOrderNo(), e));
            insertList = shopifyOrderList.stream().filter(e -> !idSet.containsKey(e.getId()) ).collect(Collectors.toList());
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
                    this.shopifyOrderinfoService.save(xmsShopifyOrderinfo);

                    if (CollectionUtil.isNotEmpty(orderInfo.getLine_items())) {
                        // 删除原来数据
                        this.shopifyOrderDetailsService.deleteByOrderNo(orderInfo.getId());
                        for (Line_items item : orderInfo.getLine_items()) {
                            item.setOrder_no(orderInfo.getId());

                            // shopifyOrderMapper.insertOrderDetails(item);

                            XmsShopifyOrderDetails xmsShopifyOrderDetails = this.genXmsShopifyOrderDetails(item);
                            this.shopifyOrderDetailsService.save(xmsShopifyOrderDetails);
                        }
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
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("shopifyName:" + shopifyName + ",genShopifyOrderInfo error:", e);
                }
            }
        }
        shopifyOrderList.clear();
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
        shopifyOrderinfo.setCreatedAt(orderInfo.getCreated_at());
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
