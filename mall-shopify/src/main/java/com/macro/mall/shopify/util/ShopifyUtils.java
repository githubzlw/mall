package com.macro.mall.shopify.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.google.gson.Gson;
import com.macro.mall.entity.XmsShopifyOrderAddress;
import com.macro.mall.entity.XmsShopifyOrderDetails;
import com.macro.mall.entity.XmsShopifyOrderinfo;
import com.macro.mall.shopify.config.ShopifyConfig;
import com.macro.mall.shopify.config.ShopifyUtil;
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
    private ShopifyUtil shopifyUtil;
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
        String json = this.shopifyUtil.exchange(url, accessToken);
        OrdersWraper result = new Gson().fromJson(json, OrdersWraper.class);
        return result;
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
            existList.forEach(e -> idSet.put(e.getId(), e));
            insertList = shopifyOrderList.stream().filter(e -> {
                if (idSet.containsKey(e.getId())) {
                    XmsShopifyOrderinfo tempOrder = idSet.get(e.getId());
                    if (!tempOrder.getTotalPriceUsd().equalsIgnoreCase(e.getTotal_price_usd())
                            || !tempOrder.getFinancialStatus().equalsIgnoreCase(e.getFinancial_status())) {
                        return true;
                    }
                    return false;
                } else {
                    return true;
                }
            }).collect(Collectors.toList());
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
