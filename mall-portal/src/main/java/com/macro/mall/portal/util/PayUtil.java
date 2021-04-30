package com.macro.mall.portal.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.config.MicroServiceConfig;
import com.macro.mall.portal.domain.PayPalParam;
import com.macro.mall.portal.domain.SiteEnum;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.RelatedResources;
import com.paypal.api.payments.Sale;
import com.paypal.api.payments.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
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
            return JSONObject.parseObject(jsonObject.toJSONString(), CommonResult.class);
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


    /**
     * Returns paymentData JsonObject for call to Execute Payment REST API for all flows
     */
    public JsonObject getExpressCheckoutJsonDataForDoPayment(HttpSession session, String payer_id, boolean shippingUpdate) {
        JsonObject paymentData;
        if (shippingUpdate == true) {
            paymentData = Json.createObjectBuilder().add("payer_id", payer_id).add("transactions", Json.createArrayBuilder().add(Json.createObjectBuilder().add("amount", Json.createObjectBuilder().add("currency", session.getAttribute("currencyCodeType") == null ? "USD" : session.getAttribute("currencyCodeType").toString()).add("total", session.getAttribute("totalAmountDefault") == null ? "0" : session.getAttribute("totalAmountDefault").toString()).add("details", Json.createObjectBuilder().add("shipping", session.getAttribute("shippingAmountDefault") == null ? "0" : session.getAttribute("shippingAmountDefault").toString()).add("subtotal", session.getAttribute("totalAmountDefault") == null ? "0" : session.getAttribute("totalAmountDefault").toString()).add("tax", "5").add("insurance", "10").add("handling_fee", "5").add("shipping_discount", "-2"))))).build();
        } else {
            paymentData = Json.createObjectBuilder().add("payer_id", payer_id).build();
        }

        return paymentData;
    }


    /**
     * 充值回调
     *
     * @param request
     * @param response
     */
    public void rechargeCallback(HttpServletRequest request, HttpServletResponse response, Map<String, String> payerInfoMap) {

        String description = (String) payerInfoMap.get("description");
        String itemName = (String) payerInfoMap.get("name");// 商品名
        String paymentStatus = (String) payerInfoMap.get("paymentStatus");// 交易状态
        String paymentDate = (String) payerInfoMap.get("paymentDate");// 交易时间
        String paymentAmount = (String) payerInfoMap.get("totalAmount");// 交易钱数
        String paymentCurrency = (String) payerInfoMap.get("currencyCode");// 货币种类
        String txnId = (String) payerInfoMap.get("txnId");// 交易id
        String payerEmail = (String) payerInfoMap.get("payerEmail");// 付款人email
        String paymentNo = (String) payerInfoMap.get("paymentNo");
        String paypalid = (String) payerInfoMap.get("paypalid");
        String transaction_fee = (String) payerInfoMap.get("transaction_fee");


        String[] customArray = description.split("\\{@\\}");
        if ((customArray.length < 4) && (!"Account Recharge".equals(itemName))) {
            return;
        }
        int userId = Integer.parseInt(customArray[0]);
        String userName = customArray[1];
        String orderNo = customArray[2];
        String paySID = customArray[3];

        log.warn("--------------paymentId:" + txnId);
        log.warn("--------------userId:" + userId);
        log.warn("--------------userName:" + userName);
        log.warn("--------------orderNo:" + orderNo);
        log.warn("--------------paySID:" + paySID);
        log.warn("--------------paymentStatus:" + paymentStatus);

        int paybtype = 1;// 1:paypal

        BigDecimal b = new BigDecimal(paymentAmount);
        BigDecimal mc_gross = b.setScale(2, BigDecimal.ROUND_HALF_UP);

        // 保存支付信息
    }

    public String validatePayment(HttpServletRequest request, HttpServletResponse response, Map<String, String> payerInfoMap) {
        int flag = 0;
        //获取是否是b2c
        Object b2C = request.getSession().getAttribute("B2C");
        if (null != b2C) {
            flag = 4;
        }
        String payflag = "";
        int state_balance = 0;//是否余额支付
        double product_cost = 0;//商品总金额
        double couponDis = 0;//优惠券折扣
//			String orderNos = null;
        int userid = 0;
        String paySID = "";
        double payAmount = 0;
        String description = (String) payerInfoMap.get("description");
        //dropship标志字段
        String dropshipflag = null;
        //cookie 里面获取用户信息
        String userName = "";
        String[] userinfo = null;
        if (userinfo != null) {
            userid = Integer.parseInt(userinfo[0]);
            userName = String.valueOf(userinfo[1]);
        }
        if (description.indexOf("@") > 0) {
            String[] custom = description.split("@");
            userid = Integer.parseInt(custom[0]);// 付款人id
            paySID = custom[1];
            payflag = custom[3];
            if (custom.length > 7) {
                //dropship标志字段 等于1表示是dropship订单的付款
                dropshipflag = custom[7];
            }
            if (custom.length > 5 && StrUtil.isNotEmpty(custom[5])) {
                product_cost = Double.parseDouble(custom[5].toString());
                if ("1".equals(custom[4])) {
                    state_balance = 1;
                }
            }
            if (custom.length > 8) {
                product_cost = Double.parseDouble(custom[8].toString());
            }
            if (custom.length > 9) {
                couponDis = Double.parseDouble(custom[9].toString());
            }
        }
        //用户的name ，注册名字
        String itemName = userName;
        //订单号
        String itemNumber = (String) payerInfoMap.get("itemId");
        // 交易状态
        String paymentStatus = (String) payerInfoMap.get("paymentStatus") == null ? "0" : (String) payerInfoMap.get("paymentStatus");
        // 交易时间
        String paymentDate = (String) payerInfoMap.get("paymentDate");
        //paypal实际支付金额
        //交易钱数
        String paymentAmount = (String) payerInfoMap.get("totalAmount");
        // 货币种类
        String paymentCurrency = (String) payerInfoMap.get("currencyCode");
        // 交易id
        String txnId = (String) payerInfoMap.get("txnId");
        // 收款人email
        String receiverEmail = (String) payerInfoMap.get("receiverEmail");
        // 付款人email
        String payerEmail = (String) payerInfoMap.get("payerEmail");
        String transaction_fee = (String) payerInfoMap.get("transaction_fee");
        String paymentNo = (String) payerInfoMap.get("paymentNo");
        String paypalid = (String) payerInfoMap.get("paypalid");

        String case_type = request.getParameter("case_type");
        String reason_code = request.getParameter("reason_code");
        String memo = request.getParameter("memo");
        String ipnAddressJson = null;//记录支付人信息

        //交易类型（1：paypal；2：stripe）
        int paybtype = 1;// 1:paypal 2:stripe
        if ("2".equals(payerInfoMap.get("payType"))) {
            paybtype = 2;
        }


        return "1";
    }

    /**
     * Parses common Payer Information fields and Payment Data returned by
     * PayPal
     */
    public Map<String, String> getCommonPayerInfoFields(com.paypal.api.payments.Payment payment) {
        {
            Map<String, String> payerInfoMap = new HashMap<String, String>(50);
            try {
                com.paypal.api.payments.PayerInfo payerinfo = payment.getPayer().getPayerInfo();
                log.debug("payerinfo:" + payerinfo.toJSON());
                //付款人email
                String email = payerinfo.getEmail();
                //付款人名
                String firstName = payerinfo.getFirstName();
                //付款人姓
                String lastName = payerinfo.getLastName();
                //付款地址内容y
                String shipToRecipientName = "";
//                ShippingAddress shippingAddress = payerinfo.getShippingAddress();
//                shipToRecipientName = shippingAddress.getRecipientName();
//                String shipToAddressLine1 = shippingAddress.getLine1();
//                String shipToCity = shippingAddress.getCity();
//                String shipToState = shippingAddress.getState();
//                String shipToPostalCode = shippingAddress.getPostalCode();
//                String shipToCountryCode = shippingAddress.getCountryCode();
                //交易信息list
                List<Transaction> transactions = payment.getTransactions();
                Assert.notNull(transactions);
                Assert.isTrue(transactions.size() > 0);

                Transaction firstTrans = transactions.get(0);
                //获取我们自定义字段数据
                String custom = firstTrans.getCustom();
                //付款金额
                String totalAmount = firstTrans.getAmount().getTotal();
                //付款货币单位
                String currencyCode = firstTrans.getAmount().getCurrency();
                String shippingAmount = firstTrans.getAmount().getDetails().getShipping();
                //去掉描述内容
                /*String description = transactions.get(0).getDescription().replaceAll("OrderNumber: ","");*/
                //付款订单内容
                List<Item> items = firstTrans.getItemList().getItems();
                Item item = items.get(0);
                //订单号 || 产品名字
                String name = item.getName();
                //应该是付款笔数 || 产品数量
                String quantity = item.getQuantity();
                List<RelatedResources> relatedResources = firstTrans.getRelatedResources();
                Assert.notNull(relatedResources);
                Assert.isTrue(relatedResources.size() > 0);
                Sale sale = relatedResources.get(0).getSale();
                Assert.notNull(sale);
                String paymentStatus = sale.getState();
                String paymentDate = sale.getCreateTime();
                String transaction_fee = "0";

                if (sale.getTransactionFee() != null
                        && StringUtils.isNotEmpty(sale.getTransactionFee().getValue())) {
                    // 手续费
                    transaction_fee = sale.getTransactionFee().getValue();
                } else {
                    // 手续费
                    transaction_fee = sale.getAmount().getDetails().getHandlingFee();
                }

                // 交易id
                String txnId = sale.getId();
                // 支付ID
                String paymentNo = payment.getId();
                String receiverEmail = "";
                /*String verified = "";*/
                boolean emailIsTrue = StrUtil.isNotBlank(firstTrans.getPayee().getEmail());
                if (emailIsTrue) {
                    // 收款人email
                    receiverEmail = firstTrans.getPayee().getEmail();
                }
                // 付款人email
                String payerEmail = payerinfo.getEmail();

                payerInfoMap.put("email", email);
                payerInfoMap.put("firstName", firstName);
                payerInfoMap.put("lastName", lastName);
                payerInfoMap.put("shipToRecipientName", shipToRecipientName);
//                payerInfoMap.put("shipToAddressLine1", shipToAddressLine1);
//                payerInfoMap.put("shipToCity", shipToCity);
//                payerInfoMap.put("shipToState", shipToState);
//                payerInfoMap.put("shipToPostalCode", shipToPostalCode);
//                payerInfoMap.put("shipToCountryCode", shipToCountryCode);
                payerInfoMap.put("paymentNo", paymentNo);
                payerInfoMap.put("totalAmount", totalAmount);
                payerInfoMap.put("currencyCode", currencyCode);
                payerInfoMap.put("shippingAmount", shippingAmount);
                payerInfoMap.put("description", custom);
                payerInfoMap.put("transaction_fee", transaction_fee);
                payerInfoMap.put("paymentStatus", paymentStatus);
                payerInfoMap.put("paymentDate", paymentDate);
                payerInfoMap.put("txnId", txnId);
                payerInfoMap.put("receiverEmail", receiverEmail);
                payerInfoMap.put("payerEmail", payerEmail);
                payerInfoMap.put("quantity", quantity);
                /* payerInfoMap.put("verified", verified);*/
                payerInfoMap.put("paypalid", payment.getId());
                payerInfoMap.put("orderNo", payment.getId());

                payerInfoMap.put("itemId", name);
            } catch (Exception e) {
                log.error("paypal传回数据有问题：payment:[{}],error:", payment, e);
            }
            return payerInfoMap;

        }
    }


    public synchronized void pay1(HttpServletRequest request, HttpServletResponse response, String payflag, String str, String str2, String itemNumber, int state_balance, double product_cost) throws IOException {

        String appConfig_paypal_business = "584JZVFU6PPVU";
        String appConfig_paypal_action = "https://www.paypal.com/cgi-bin/webscr";
        SiteEnum siteEnum = SiteEnum.SOURCING;
        // 检查是否重复支付
        System.err.println("---paycontroller-----------payflag:" + payflag);
        System.err.println("-----paycontroller----------str:" + str);
        System.err.println("-----paycontroller----------str2:" + str2);
        /*if (appConfig_paypal_business.contains("@")) {
            TlsCheck.supportTls12();
            TlsCheck.testTls12Connection();
        }*/
        // System.out.println("paypal validate info:" + str);
        // 建议在此将接受到的信息 str 记录到日志文件中以确认是否收到 IPN 信息
        // 将信息 POST 回给 PayPal 进行验证
        // 设置 HTTP 的头信息
        // 在 Sandbox 情况下，设置：
        URL u = new URL(appConfig_paypal_action);
        // 正式环境
        // URL u = new URL("https://www.paypal.com/cgi-bin/webscr");

        URLConnection uc = u.openConnection();
        uc.setDoOutput(true);
        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        PrintWriter pw = new PrintWriter(uc.getOutputStream());
        pw.println(str2);
        pw.close();
        // 接受 PayPal 对 IPN 回发的回复信息
        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String res = in.readLine();
        System.err.println("-------paycontroller--------res:" + res);
        in.close();
        // 将 POST 信息分配给本地变量，可以根据您的需要添加
        // 该付款明细所有变量可参考：

        // https://www.paypal.com/IntegrationCenter/ic_ipn-pdt-variable-reference.html
        String itemName = request.getParameter("item_name");// 商品名
        // --这里存入的是用户名
        if (StrUtil.isNotEmpty(itemNumber)) {
            itemNumber = request.getParameter("item_number");// 购买数量
            // --这里存入的是订单号
        }
//		String itemNumber = request.getParameter("item_number");// 购买数量
        // --这里存入的是订单号
        String paymentStatus = request.getParameter("payment_status");// 交易状态
        String paymentDate = request.getParameter("payment_date");// 交易时间
        String paymentAmount = request.getParameter("mc_gross");// 交易钱数
        String mc_fee = request.getParameter("mc_fee");// 费用
        String paymentCurrency = request.getParameter("mc_currency");// 货币种类
        String txnId = request.getParameter("txn_id");// 交易id
        String receiverEmail = request.getParameter("receiver_email");// 收款人email
        String payerEmail = request.getParameter("payer_email");// 付款人email

        String address_status = request.getParameter("address_status");
        String residence_country = request.getParameter("residence_country");
        String address_country = request.getParameter("address_country");
        String address_city = request.getParameter("address_city");
        String address_country_code = request.getParameter("address_country_code");
        String address_state = request.getParameter("address_state");
        String address_name = request.getParameter("address_name");
        String address_street = request.getParameter("address_street");
        String case_type = request.getParameter("case_type");
        String reason_code = request.getParameter("reason_code");
        String memo = request.getParameter("buyer_additional_information");//rease


        Map<String, String> ipnAddressMap = new HashMap<String, String>();
        ipnAddressMap.put("address_status", address_status);
        ipnAddressMap.put("residence_country", residence_country);
        ipnAddressMap.put("address_country", address_country);
        ipnAddressMap.put("address_city", address_city);
        ipnAddressMap.put("address_country_code", address_country_code);
        ipnAddressMap.put("address_state", address_state);
        ipnAddressMap.put("address_name", address_name);
        ipnAddressMap.put("address_street", address_street);
        ipnAddressMap.put("receiverEmail", receiverEmail);
        String ipnAddressJson = new Gson().toJson(ipnAddressMap);
        int userid = 0;
        String paySID = "";
        if (request.getParameter("custom") != null && request.getParameter("custom").indexOf("@") > 0) {
            String[] custom = request.getParameter("custom").split("@");
            userid = Integer.parseInt(custom[0]);// 付款人id
            paySID = custom[1];
        }
        int paybtype = 1;// 1:paypal

        if (res == null || res == "") res = "0";


        if (res.equals("VERIFIED")) {
            System.err.println("------paycontroller----------该订单号" + itemNumber + "支付成功");
            // 检查付款状态
            // 检查 txn_id 是否已经处理过
            // 检查 receiver_email 是否是您的 PayPal 账户中的 EMAIL 地址
            // 检查付款金额和货币单位是否正确
            // 处理其他数据，包括写数据库

        } else {
            // 非法信息，可以将此记录到您的日志文件中以备调查
            System.err.println("-----paycontroller-----------该订单号" + itemNumber + "支付成功，但支付信息存在问题");
        }


    }

}
