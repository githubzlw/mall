package com.macro.mall.portal.util;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.config.MicroServiceConfig;
import com.macro.mall.portal.domain.PayPalParam;
import com.macro.mall.portal.domain.SiteEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付util
 */
@Slf4j
@Service
public class PayUtil {
    private static UrlUtil instance = UrlUtil.getInstance();
    @Autowired
    private MicroServiceConfig microServiceConfig;

    /**
     * getPayPalRedirectUtlByPayInfo
     *
     * @param payPalParam
     */
    public CommonResult getPayPalRedirectUtlByPayInfo(PayPalParam payPalParam) {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("cancelUrlType", String.valueOf(payPalParam.getCancelUrlType()));
        requestMap.put("total", String.valueOf(payPalParam.getTotalAmount()));
        requestMap.put("orderNo", payPalParam.getOrderNo());
        requestMap.put("customMsg", payPalParam.getCustomMsg());
        requestMap.put("successUrl", payPalParam.getSuccessUrl());
        requestMap.put("cancelUrl", payPalParam.getCancelUrl());
        try {
            String resUrl = microServiceConfig.getUrl() + UrlUtil.MICRO_SERVICE_PAY + "paypal/" + payPalParam.getSiteName() + "/create/";
            JSONObject jsonObject = instance.postURL(resUrl, requestMap);
            CommonResult commonResult = new Gson().fromJson(jsonObject.toJSONString(), CommonResult.class);
            return commonResult;
        } catch (IOException e) {
            log.error("getPayPalRedirectUtlByPayInfo,payPalParam:[{}],error:", payPalParam, e);
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * execute : execute
     */
    public CommonResult execute(String paymentId, String payerId, SiteEnum siteEnum) {
        CommonResult commonResult;
        String siteName = siteEnum.getName();
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("paymentId", paymentId);
        requestMap.put("payerId", payerId);
        try {
            JSONObject jsonObject = instance.postURL(microServiceConfig.getUrl() + UrlUtil.MICRO_SERVICE_PAY + "paypal/" + siteName + "/execute/", requestMap);
            commonResult = new Gson().fromJson(jsonObject.toJSONString(), CommonResult.class);
            if (commonResult.getCode() == 200) {
                CommonResult.success(new Gson().fromJson(commonResult.getData().toString(), CommonResult.class));
            }
        } catch (IOException e) {
            log.error("paymentId :[{}],payerId:[{}]", paymentId, payerId);
            log.error("pay pal execute", e);
            return CommonResult.failed(e.getMessage());
        }
        return commonResult;
    }

    /**
     * getCommonPayerInfo : getCommonPayerInfo
     */
    public HashMap<String, String> getCommonPayerInfo(String data) {
        com.google.gson.JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        HashMap<String, String> payerInfoMap = new HashMap<String, String>();

        try {
            com.google.gson.JsonObject payerinfo = jsonObject.get("payer").getAsJsonObject().get("payer_info").getAsJsonObject();
            String email = payerinfo.get("email").getAsString();
            String firstName = payerinfo.get("first_name").getAsString();
            String lastName = payerinfo.get("last_name").getAsString();
            String shipToRecipientName = "";
            JsonElement shipping_address = payerinfo.get("shipping_address");
            if (shipping_address.getAsJsonObject().has("recipient_name")) {
                shipToRecipientName = shipping_address.getAsJsonObject().get("recipient_name").getAsString();
            }
            String shipToAddressLine1 = shipping_address.getAsJsonObject().get("line1").getAsString();
            String shipToCity = shipping_address.getAsJsonObject().get("city").getAsString();
            String shipToState = shipping_address.getAsJsonObject().get("state").getAsString();
            String shipToPostalCode = shipping_address.getAsJsonObject().get("postal_code").getAsString();
            String shipToCountryCode = shipping_address.getAsJsonObject().get("country_code").getAsString();

            com.google.gson.JsonObject transactions = jsonObject.get("transactions").getAsJsonArray().get(0).getAsJsonObject();
            com.google.gson.JsonObject amount = transactions.get("amount").getAsJsonObject();
            String totalAmount = amount.get("total").getAsString();
            String currencyCode = amount.get("currency").getAsString();
            String shippingAmount = amount.get("details").getAsJsonObject().get("shipping").getAsString();

            String description = transactions.get("description").getAsString();

            com.google.gson.JsonObject items = transactions.get("item_list").getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject();
            String sku = "";
            try {
                sku = items.get("sku").getAsString();
            } catch (Exception e) {
                log.error("sku is null email:{}", email);
            }
            String name = items.get("name").getAsString();
            String quantity = "";
            try {
                quantity = items.get("quantity").getAsInt() + "";
            } catch (Exception e) {
                quantity = items.get("quantity").getAsString();
                log.error("quantity is not int email:{}", email);
            }


            com.google.gson.JsonObject sale = transactions.get("related_resources").getAsJsonArray().get(0).getAsJsonObject().get("sale").getAsJsonObject();
            String paymentStatus = sale.get("state").getAsString();
            String paymentDate = sale.get("create_time").getAsString();
            String transaction_fee = "0";
            if (sale.get("transaction_fee") != null) {
                transaction_fee = sale.get("transaction_fee").getAsJsonObject().get("value").getAsString();
                ;// 手续费
            }
            String txnId = sale.get("id").getAsString();// 交易id
            String paymentNo = jsonObject.get("id").getAsString();// 支付ID
            String receiverEmail = "";
            if (transactions.has("payee")) {
                receiverEmail = transactions.get("payee").getAsJsonObject().get("email").getAsString();// 收款人email
            }
            String payerEmail = payerinfo.get("email").getAsString();// 付款人email

            payerInfoMap.put("email", email);
            payerInfoMap.put("firstName", firstName);
            payerInfoMap.put("lastName", lastName);
            payerInfoMap.put("shipToRecipientName", shipToRecipientName);
            payerInfoMap.put("shipToAddressLine1", shipToAddressLine1);
            payerInfoMap.put("shipToCity", shipToCity);
            payerInfoMap.put("shipToState", shipToState);
            payerInfoMap.put("shipToPostalCode", shipToPostalCode);
            payerInfoMap.put("shipToCountryCode", shipToCountryCode);
            payerInfoMap.put("paymentNo", paymentNo);
            payerInfoMap.put("totalAmount", totalAmount);
            payerInfoMap.put("currencyCode", currencyCode);
            payerInfoMap.put("shippingAmount", shippingAmount);
            payerInfoMap.put("description", description);
            payerInfoMap.put("sku", sku);
            payerInfoMap.put("name", name);
            payerInfoMap.put("itemId", name);
            payerInfoMap.put("transaction_fee", transaction_fee);
            payerInfoMap.put("paymentStatus", paymentStatus);
            payerInfoMap.put("paymentDate", paymentDate);
            payerInfoMap.put("txnId", txnId);
            payerInfoMap.put("receiverEmail", receiverEmail);
            payerInfoMap.put("payerEmail", payerEmail);
            payerInfoMap.put("quantity", quantity);
            payerInfoMap.put("paypalid", jsonObject.get("id").getAsString());
        } catch (Exception e) {
            log.error("==============================paypal传回数据有问题：" + jsonObject + "错误信息" + e.getMessage());
        }
        return payerInfoMap;

    }

}
