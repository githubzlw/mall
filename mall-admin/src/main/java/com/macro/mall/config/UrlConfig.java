package com.macro.mall.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.config
 * @date:2021-09-13
 */
@Configuration
@Data
public class UrlConfig {

    @Value("${shopifyApi.url}")
    public String shopifyApiUrl;
}
