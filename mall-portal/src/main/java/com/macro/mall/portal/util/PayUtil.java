package com.macro.mall.portal.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.entity.XmsPayment;
import com.macro.mall.entity.XmsPaymentLog;
import com.macro.mall.entity.XmsRecordOfChangeInBalance;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.mapper.XmsPaymentLogMapper;
import com.macro.mall.mapper.XmsRecordOfChangeInBalanceMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderExample;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.cache.RedisUtil;
import com.macro.mall.portal.config.MicroServiceConfig;
import com.macro.mall.portal.config.PayConfig;
import com.macro.mall.portal.domain.GenerateOrderResult;
import com.macro.mall.portal.domain.PayPalParam;
import com.macro.mall.common.enums.SiteEnum;
import com.macro.mall.portal.enums.PayFromEnum;
import com.macro.mall.portal.enums.PayStatusEnum;
import com.macro.mall.portal.enums.PayTypeEnum;
import com.macro.mall.portal.service.IXmsPaymentService;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.RelatedResources;
import com.paypal.api.payments.Sale;
import com.paypal.api.payments.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * ??????util
 */
@Slf4j
@Service
public class PayUtil {
    private static UrlUtil instance = UrlUtil.getInstance();

    @Autowired
    private UmsMemberMapper memberMapper;
    @Autowired
    private MicroServiceConfig microServiceConfig;
    @Autowired
    private IXmsPaymentService xmsPaymentService;
    @Autowired
    private PayConfig payConfig;
    @Autowired
    private XmsRecordOfChangeInBalanceMapper xmsRecordOfChangeInBalanceMapper;
    @Autowired
    private OrderUtils orderUtils;
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private XmsPaymentLogMapper xmsPaymentLogMapper;
    @Autowired
    private ExchangeRateUtils exchangeRateUtils;

    /**
     * getPayPalRedirectUtlByPayInfo
     *
     * @param payPalParam
     */
    public CommonResult getPayPalRedirectUtlByPayInfo(PayPalParam payPalParam, RedisUtil redisUtil) {

        Object val = redisUtil.hget(OrderUtils.PAY_USER_ID, String.valueOf(payPalParam.getMemberId()));
        if (null != val) {
            return CommonResult.failed("There is an order to be paid. Please try again later");
        }

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("cancelUrlType", String.valueOf(this.payConfig.getCancelUrlType()));
        requestMap.put("total", String.valueOf(payPalParam.getTotalAmount()));
        requestMap.put("orderNo", payPalParam.getOrderNo());
        requestMap.put("customMsg", payPalParam.getCustomMsg());
        requestMap.put("successUrl", this.payConfig.getSuccessUrl());
        requestMap.put("cancelUrl", payConfig.getCancelUrl());
        try {
            String resUrl = this.microServiceConfig.getPayUrl() + "/" + payPalParam.getSiteName() + "/create/";
            JSONObject jsonObject = instance.postURL(resUrl, requestMap);
            CommonResult commonResult = JSONObject.parseObject(jsonObject.toJSONString(), CommonResult.class);
            if (commonResult.getCode() == 200) {
                redisUtil.hset(OrderUtils.PAY_USER_ID, String.valueOf(payPalParam.getMemberId()), payPalParam.getOrderNo(), RedisUtil.EXPIRATION_TIME_5_MINUTES);
                Map<String, String> param = new HashMap<>();
                param.put("balanceFlag", "0");
                param.put("payUrl", commonResult.getData().toString());
                return CommonResult.success(param);
            }
            return commonResult;
        } catch (IOException e) {
            log.error("getPayPalRedirectUtlByPayInfo,payPalParam:[{}],error:", payPalParam, e);
            redisUtil.hdel(OrderUtils.PAY_USER_ID, String.valueOf(payPalParam.getMemberId()));
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
            JSONObject jsonObject = instance.postURL(this.microServiceConfig.getPayUrl() + "/" + siteName + "/execute/", requestMap);
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
                ;// ?????????
            }
            String txnId = sale.get("id").getAsString();// ??????id
            String paymentNo = jsonObject.get("id").getAsString();// ??????ID
            String receiverEmail = "";
            if (transactions.has("payee")) {
                receiverEmail = transactions.get("payee").getAsJsonObject().get("email").getAsString();// ?????????email
            }
            String payerEmail = payerinfo.get("email").getAsString();// ?????????email

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
            log.error("==============================paypal????????????????????????" + jsonObject + "????????????" + e.getMessage());
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
     * ????????????
     *
     * @param request
     * @param response
     */
    public void rechargeCallback(HttpServletRequest request, HttpServletResponse response, Map<String, String> payerInfoMap) {

        String description = (String) payerInfoMap.get("description");
        String itemName = (String) payerInfoMap.get("name");// ?????????
        String paymentStatus = (String) payerInfoMap.get("paymentStatus");// ????????????
        String paymentDate = (String) payerInfoMap.get("paymentDate");// ????????????
        String paymentAmount = (String) payerInfoMap.get("totalAmount");// ????????????
        String paymentCurrency = (String) payerInfoMap.get("currencyCode");// ????????????
        String txnId = (String) payerInfoMap.get("txnId");// ??????id
        String payerEmail = (String) payerInfoMap.get("payerEmail");// ?????????email
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

        // ??????????????????
    }

    public String validatePayment(HttpServletRequest request, HttpServletResponse response, Map<String, String> payerInfoMap) {
        int flag = 0;
        //???????????????b2c
        Object b2C = request.getSession().getAttribute("B2C");
        if (null != b2C) {
            flag = 4;
        }
        String payflag = "";
        int state_balance = 0;//??????????????????
        double product_cost = 0;//???????????????
        double couponDis = 0;//???????????????
//			String orderNos = null;
        int userid = 0;
        String paySID = "";
        double payAmount = 0;
        String description = (String) payerInfoMap.get("description");
        //dropship????????????
        String dropshipflag = null;
        //cookie ????????????????????????
        String userName = "";
        String[] userinfo = null;
        if (userinfo != null) {
            userid = Integer.parseInt(userinfo[0]);
            userName = String.valueOf(userinfo[1]);
        }
        if (description.indexOf("@") > 0) {
            String[] custom = description.split("@");
            userid = Integer.parseInt(custom[0]);// ?????????id
            paySID = custom[1];
            payflag = custom[3];
            if (custom.length > 7) {
                //dropship???????????? ??????1?????????dropship???????????????
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
        //?????????name ???????????????
        String itemName = userName;
        //?????????
        String itemNumber = (String) payerInfoMap.get("itemId");
        // ????????????
        String paymentStatus = (String) payerInfoMap.get("paymentStatus") == null ? "0" : (String) payerInfoMap.get("paymentStatus");
        // ????????????
        String paymentDate = (String) payerInfoMap.get("paymentDate");
        //paypal??????????????????
        //????????????
        String paymentAmount = (String) payerInfoMap.get("totalAmount");
        // ????????????
        String paymentCurrency = (String) payerInfoMap.get("currencyCode");
        // ??????id
        String txnId = (String) payerInfoMap.get("txnId");
        // ?????????email
        String receiverEmail = (String) payerInfoMap.get("receiverEmail");
        // ?????????email
        String payerEmail = (String) payerInfoMap.get("payerEmail");
        String transaction_fee = (String) payerInfoMap.get("transaction_fee");
        String paymentNo = (String) payerInfoMap.get("paymentNo");
        String paypalid = (String) payerInfoMap.get("paypalid");

        String case_type = request.getParameter("case_type");
        String reason_code = request.getParameter("reason_code");
        String memo = request.getParameter("memo");
        String ipnAddressJson = null;//?????????????????????

        //???????????????1???paypal???2???stripe???
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
                //?????????email
                String email = payerinfo.getEmail();
                //????????????
                String firstName = payerinfo.getFirstName();
                //????????????
                String lastName = payerinfo.getLastName();
                //??????????????????y
                String shipToRecipientName = "";
//                ShippingAddress shippingAddress = payerinfo.getShippingAddress();
//                shipToRecipientName = shippingAddress.getRecipientName();
//                String shipToAddressLine1 = shippingAddress.getLine1();
//                String shipToCity = shippingAddress.getCity();
//                String shipToState = shippingAddress.getState();
//                String shipToPostalCode = shippingAddress.getPostalCode();
//                String shipToCountryCode = shippingAddress.getCountryCode();
                //????????????list
                List<Transaction> transactions = payment.getTransactions();
                Assert.notNull(transactions);
                Assert.isTrue(transactions.size() > 0);

                Transaction firstTrans = transactions.get(0);
                //?????????????????????????????????
                String custom = firstTrans.getCustom();
                //????????????
                String totalAmount = firstTrans.getAmount().getTotal();
                //??????????????????
                String currencyCode = firstTrans.getAmount().getCurrency();
                String shippingAmount = firstTrans.getAmount().getDetails().getShipping();
                //??????????????????
                /*String description = transactions.get(0).getDescription().replaceAll("OrderNumber: ","");*/
                //??????????????????
                List<Item> items = firstTrans.getItemList().getItems();
                Item item = items.get(0);
                //????????? || ????????????
                String name = item.getName();
                //????????????????????? || ????????????
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
                    // ?????????
                    transaction_fee = sale.getTransactionFee().getValue();
                } else {
                    // ?????????
                    transaction_fee = sale.getAmount().getDetails().getHandlingFee();
                }

                // ??????id
                String txnId = sale.getId();
                // ??????ID
                String paymentNo = payment.getId();
                String receiverEmail = "";
                /*String verified = "";*/
                boolean emailIsTrue = StrUtil.isNotBlank(firstTrans.getPayee().getEmail());
                if (emailIsTrue) {
                    // ?????????email
                    receiverEmail = firstTrans.getPayee().getEmail();
                }
                // ?????????email
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
                log.error("paypal????????????????????????payment:[{}],error:", payment, e);
            }
            return payerInfoMap;

        }
    }


    /**
     * ?????????????????????????????????PayPal??????
     *
     * @param orderResult
     * @param currentMember
     * @param request
     * @return
     */
    public CommonResult beforePayAndPay(GenerateOrderResult orderResult, UmsMember currentMember, HttpServletRequest request, PayFromEnum payFromEnum, RedisUtil redisUtil) {
        // ???????????????????????????????????????
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(orderResult.getOrderNo());

        // 1. ???PayPal????????? 2. PayPal????????????????????? 3. ???????????????
        String paymentId = "";
        double payAmount = orderResult.getPayAmount();
        if (orderResult.getPayAmount() > 0) {
            omsOrder.setNote(JSONObject.toJSONString(orderResult) + "||paypal???????????????");
            omsOrder.setTotalAmount(new BigDecimal(payAmount));
            omsOrder.setPayType(0);
            omsOrder.setStatus(0);
            this.insertPaymentLog(currentMember, paymentId, payFromEnum, omsOrder);
        }
        if (orderResult.getBalanceAmount() > 0) {
            omsOrder.setNote(JSONObject.toJSONString(orderResult) + "||?????????????????????");
            omsOrder.setTotalAmount(new BigDecimal(orderResult.getBalanceAmount()));
            omsOrder.setPayType(1);
            omsOrder.setStatus(0);
            this.insertPaymentLog(currentMember, paymentId, payFromEnum, omsOrder);

        }
        if (orderResult.getPayAmount() > 0) {
            PayPalParam payPalParam = this.getPayPalParam(request, currentMember.getId(), orderResult.getOrderNo(), orderResult.getPayAmount());
            payPalParam.setSuccessUrlType("1");
            payPalParam.setMemberId(currentMember.getId());
            return this.getPayPalRedirectUtlByPayInfo(payPalParam, redisUtil);
        } else {
            orderResult.setBalanceFlag(1);
            // ??????????????????
            this.orderUtils.paySuccessUpdate(orderResult.getOrderNo(), 1);

            // ??????????????????
            this.payBalance(orderResult.getBalanceAmount(), currentMember, 0, orderResult.getOrderNo(), "", payFromEnum);
            return CommonResult.success(orderResult, "Balance paid successfully");
        }
    }

    /**
     * ?????????????????????
     *
     * @param amount
     * @param currentMember
     * @param operatingType ???????????? 0:???????????? 1:????????????
     * @return
     */
    @Transactional
    public int payBalance(Double amount, UmsMember currentMember, Integer operatingType, String orderNo, String paymentId, PayFromEnum payFromEnum) {
        synchronized (currentMember.getId()) {
            UmsMember umsMember = this.memberMapper.selectByPrimaryKey(currentMember.getId());

            UmsMember tempMember = new UmsMember();
            tempMember.setId(umsMember.getId());
            Double balance = umsMember.getBalance();
            if (null == balance) {
                balance = 0D;
            }
            if (operatingType > 0) {
                tempMember.setBalance(balance + amount);
            } else {
                tempMember.setBalance(balance - amount);
            }

            this.memberMapper.updateByPrimaryKeySelective(tempMember);
            // ??????????????????
            if (operatingType == 0) {
                this.insertPayment(currentMember, orderNo, amount, PayStatusEnum.SUCCESS, paymentId, "balance pay", PayTypeEnum.BALANCE, payFromEnum);
            }

            // ??????????????????
            XmsRecordOfChangeInBalance changeInBalance = new XmsRecordOfChangeInBalance();
            changeInBalance.setCreateTime(new Date());
            changeInBalance.setCurrentBalance(balance);
            changeInBalance.setOperatingValue(amount);
            changeInBalance.setOperatingType(operatingType);
            changeInBalance.setOperatingResult(tempMember.getBalance());
            changeInBalance.setMemberId(currentMember.getId());
            changeInBalance.setUsername(currentMember.getUsername());
            return xmsRecordOfChangeInBalanceMapper.insert(changeInBalance);
        }
    }


    /**
     * ?????????????????????????????????
     *
     * @param orderNo
     */
    public void payBalanceByOrderNo(String orderNo, Long memberId, PayFromEnum payFromEnum) {
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andOrderSnEqualTo(orderNo);
        List<OmsOrder> omsOrders = this.orderMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(omsOrders)) {
            Double balanceAmount = omsOrders.get(0).getBalanceAmount();
            if (null != balanceAmount && balanceAmount > 0) {
                UmsMember umsMember = this.memberMapper.selectByPrimaryKey(memberId);
                this.payBalance(balanceAmount, umsMember, 0, orderNo, "", payFromEnum);
            }
        }
    }


    public PayPalParam getPayPalParam(HttpServletRequest request, Long userId, String orderNo, Double totalAmount) {
        PayPalParam payPalParam = new PayPalParam();

        String paySID = UUID.randomUUID().toString();

        String appConfig_paypal_business = this.payConfig.getBusinessId();
        // int userid = 15937;
        String md = appConfig_paypal_business + userId + orderNo + totalAmount;
        String sign = Md5Util.encoder(md);
        String payflag = "O";
        String isBalance = "0";
        double credit = 0;
        int dropshipflag = 0;
        double productCost = totalAmount - 10;
        double coupon_discount = 0;

        String customMsg = userId + "@" + paySID + "@" + sign + "@" + payflag + "@" + isBalance + "@" + credit + "@" + orderNo + "@" + dropshipflag + "@" + productCost + "@" + coupon_discount;

        request.getSession().setAttribute("description", customMsg);

        payPalParam.setOrderNo(orderNo);
        payPalParam.setTotalAmount(totalAmount);
        payPalParam.setSiteName(SiteEnum.SOURCING.getName());
        payPalParam.setCustomMsg(customMsg);

        return payPalParam;
    }


    /**
     * @param currentMember
     * @param orderNo
     * @param paymentAmount
     * @param payStatusEnum : ???????????? 0 ??????(Failed) 1 ??????(Success) 2?????????(Pending)
     * @param paymentId
     * @param remark
     * @param payTypeEnum   : 0???paypal?????????1 ????????????
     */
    public void insertPayment(UmsMember currentMember, String orderNo, Double paymentAmount,
                              PayStatusEnum payStatusEnum, String paymentId, String remark, PayTypeEnum payTypeEnum, PayFromEnum payFromEnum) {
        XmsPayment xmsPayment = new XmsPayment();
        xmsPayment.setUsername(currentMember.getUsername());
        xmsPayment.setMemberId(currentMember.getId());
        xmsPayment.setOrderNo(orderNo);
        xmsPayment.setPaymentAmount(paymentAmount.floatValue());
        xmsPayment.setPayStatus(payStatusEnum.getCode());
        xmsPayment.setPaymentId(paymentId);
        xmsPayment.setRemark(remark);
        xmsPayment.setPayType(payTypeEnum.getCode());
        xmsPayment.setPayFrom(payFromEnum.getCode());
        this.xmsPaymentService.save(xmsPayment);
        System.err.println(Arrays.toString(Thread.currentThread().getStackTrace()));
    }

    /**
     * ????????????
     *
     * @param currentMember
     * @param paymentId
     * @param payFromEnum
     * @param omsOrder
     */
    public void insertPaymentLog(UmsMember currentMember, String paymentId, PayFromEnum payFromEnum, OmsOrder omsOrder) {

        XmsPaymentLog paymentLog = new XmsPaymentLog();
        paymentLog.setCreateTime(new Date());
        paymentLog.setExchangeRate(exchangeRateUtils.getUsdToCnyRate());
        paymentLog.setMemberId(currentMember.getId());
        paymentLog.setOrderInfo(JSONObject.toJSONString(omsOrder));
        paymentLog.setOrderNo(omsOrder.getOrderSn());
        paymentLog.setPayFrom(payFromEnum.getCode());
        paymentLog.setPaymentAmount(omsOrder.getTotalAmount().floatValue());
        paymentLog.setPayType(omsOrder.getPayType());
        paymentLog.setPayStatus(omsOrder.getStatus());
        paymentLog.setPaySid(paymentId);
        paymentLog.setRemark(omsOrder.getNote());
        paymentLog.setPaymentNo(omsOrder.getOrderSn());
        paymentLog.setUsername("");
        this.xmsPaymentLogMapper.insert(paymentLog);
    }

}
