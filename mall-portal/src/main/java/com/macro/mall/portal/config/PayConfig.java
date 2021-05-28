package com.macro.mall.portal.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.config
 * @date:2021-05-07
 */
@Data
@Component
public class PayConfig {

    @ApiModelProperty(value = "取消链接")
    @Value("${PAYPAL.CANCEL_URL_TYPE}")
    private Integer cancelUrlType;
    @ApiModelProperty(value = "支付成功后调用链接")
    @Value("${PAYPAL.SUCCESS_URL}")
    private String successUrl;
    @ApiModelProperty(value = "取消后调用链接")
    @Value("${PAYPAL.CANCEL_URL}")
    private String cancelUrl;
    @ApiModelProperty(value = "paypal的商户ID")
    @Value("${PAYPAL.BUSINESS_ID}")
    private String businessId;


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

    @Value("${spring.rabbitmq.host}")
    public String rabbitmqHost;

    @Value("${spring.rabbitmq.port}")
    public int rabbitmqPort;

    @Value("${spring.rabbitmq.username}")
    public String rabbitmqUser;

    @Value("${spring.rabbitmq.password}")
    public String rabbitmqPass;

    @Value("${rabbitmq.rpc.qname}")
    public String qnameRpc;
}
