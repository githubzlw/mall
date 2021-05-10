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
    @Value("${paypal.cancelUrlType}")
    private Integer cancelUrlType;
    @ApiModelProperty(value = "支付成功后调用链接")
    @Value("${paypal.successUrl}")
    private String successUrl;
    @ApiModelProperty(value = "取消后调用链接")
    @Value("${paypal.cancelUrl}")
    private String cancelUrl;
    @ApiModelProperty(value = "paypal的商户ID")
    @Value("${paypal.businessId}")
    private String businessId;
}
