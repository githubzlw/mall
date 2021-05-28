package com.macro.mall.portal.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: 微服务配置
 * @date:2021-04-15
 */
@Component
@Data
public class MicroServiceConfig {

    @Value("${localService.exchangeRateApi.url}")
    private String exchangeRateUrl;

    @Value("${localService.shopifyApi.url}")
    private String shopifyUrl;

    @Value("${localService.productApi.url}")
    private String productUrl;

    @Value("${localService.payApi.url}")
    private String payUrl;

    @Value("${localService.oneboundApi.url}")
    private String oneBoundApi;

}
