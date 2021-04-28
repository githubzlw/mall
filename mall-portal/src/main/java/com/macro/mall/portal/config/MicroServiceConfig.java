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

    @Value("${import.microService.api.url}")
    private String url;
}
