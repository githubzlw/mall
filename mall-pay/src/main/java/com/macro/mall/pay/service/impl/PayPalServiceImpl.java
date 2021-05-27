package com.macro.mall.pay.service.impl;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.pay.config.PayConfig;
import com.macro.mall.pay.enums.PayPalPaymentIntentEnum;
import com.macro.mall.pay.enums.PayPalPaymentMethodEnum;
import com.macro.mall.pay.service.PayPalService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;


/**
 * @author jack.luo
 */
@Service
@Slf4j
public class PayPalServiceImpl implements PayPalService {

    private final PayConfig payConfig;

    public PayPalServiceImpl(PayConfig payConfig) {
        this.payConfig = payConfig;
    }

    @Override
    public Payment createPayment(
            Double total,
            String cancelUrl,
            String successUrl,
            String orderNO,String customMsg
    ) throws PayPalRESTException {

        return createPayment(
                total,
                "USD",
                PayPalPaymentMethodEnum.paypal,
                PayPalPaymentIntentEnum.sale,
                "",
                cancelUrl,
                successUrl,
                orderNO,customMsg);

    }

    @Override
    public Payment createPayment(
            Double total,
            String currency,
            PayPalPaymentMethodEnum method,
            PayPalPaymentIntentEnum intent,
            String description,
            String cancelUrl,
            String successUrl,
            String orderNO,
            String customMsg) throws PayPalRESTException {

        APIContext apiContext = getApiContext();

        // ###Details
        Details details = new Details();
        details.setShipping("0");
        String strTotal = String.format("%.2f", total);
        details.setSubtotal(strTotal);
        details.setTax("0");

        // ###Amount
        Amount amount = new Amount();
        amount.setCurrency(currency);
        // Total must be equal to sum of shipping, tax and subtotal.
        amount.setTotal(strTotal);
        amount.setDetails(details);

        // ###Transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setCustom(customMsg);
        // ###Transactions
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        // ### Items
        Item item = new Item();
        item.setName(orderNO).setQuantity("1").setCurrency(currency).setPrice(strTotal);
        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<>();
        items.add(item);
        itemList.setItems(items);
        transaction.setItemList(itemList);

        // ###Payer
        Payer payer = new Payer();
        payer.setPaymentMethod(method.toString());

        // ###Payment
        Payment payment = new Payment();
        payment.setIntent(intent.toString());
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        // ###Redirect URLs
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        //不显示收货地址信息
        //payment.setExperienceProfileId(getWebProfile(apiContext));

        return payment.create(apiContext);
    }

    @Override
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {

        log.info("executePayment():[{}],[{}]", paymentId, payerId);

        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);

        return payment.execute(getApiContext(), paymentExecute);

    }

    /**
     * getApiContext
     * @return
     */
    private APIContext getApiContext() {

        Map<String, String> sdkConfig = new HashMap<>(1);
        sdkConfig.put("mode", payConfig.paypalMode);
        return new APIContext(payConfig.PaypalClientId, payConfig.PaypalClientSecret, payConfig.paypalMode, sdkConfig);
    }


    /**
     * getRandomUUID
     * @return
     */
    @Override
    public String getRandomUUID(){
        return UUID.randomUUID().toString();
    }

    /**
     * refund
     * @param saleId
     * @param amountMoney
     * @return
     */
    @Override
    public CommonResult refund(String saleId, Double amountMoney) {

        Sale sale = new Sale();
        sale.setId(saleId);

        com.paypal.api.payments.RefundRequest refund = new RefundRequest();

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.valueOf(amountMoney));
        refund.setAmount(amount);
        try {
            if(amountMoney>=300.00){
                return CommonResult.failed("The refund amount must not more then 300$ ");
            }
            DetailedRefund detailedRefund = sale.refund(getApiContext(), refund);
            try{
                Assert.isTrue(
                        amountMoney==Double.parseDouble(detailedRefund.getAmount().getTotal()),"The refund amount is not same to require");
            }catch(IllegalArgumentException iae){
                log.error("refund",iae);
            }
            log.info("detailedRefund:[{}]",detailedRefund.toJSON());
            return CommonResult.success(detailedRefund.getId()+","+detailedRefund.getAmount().getTotal());
        } catch (PayPalRESTException e) {
            return CommonResult.failed(e.getMessage());
        }
    }

}