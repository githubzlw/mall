package com.macro.mall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.config
 * @date:2021-09-27
 */
@Configuration
public class WinitConfig {

    @Value("${winit.api.key}")
    public String API_KEY;

    @Value("${winit.api.client_id}")
    public String API_CLIENT_ID;

    @Value("${winit.api.client_sign}")
    public String API_CLIENT_SIGN;

    @Value("${winit.api.token}")
    public String API_TOKEN;

    @Value("${winit.api.url}")
    public String API_URL;

    @Value("${winit.api.platform}")
    public String API_PLATFORM;

}
