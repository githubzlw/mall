package com.macro.mall.pay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayConfig {

    @Value("${PAYPAL.SANDBOX}")
    public boolean isPaypalSandbox;

    @Value("${PAYPAL.MODE}")
    public String paypalMode;

    @Value("${PAYPAL.CLIENT_ID}")
    public String PaypalClientId;

    @Value("${PAYPAL.CLIENT_SECRET}")
    public String PaypalClientSecret;

    @Value("${STRIPE.MODE}")
    public String stripeMode;

    @Value("${STRIPE.PK_KEY}")
    public String stripePk;

    @Value("${STRIPE.SK_KEY}")
    public String stripeSk;

    @Value("${rabbitmq.host}")
    public String rabbitmqHost;

    @Value("${rabbitmq.port}")
    public int rabbitmqPort;

    @Value("${rabbitmq.username}")
    public String rabbitmqUser;

    @Value("${rabbitmq.password}")
    public String rabbitmqPass;

    @Value("${rabbitmq.rpc.qname}")
    public String qnameRpc;


}