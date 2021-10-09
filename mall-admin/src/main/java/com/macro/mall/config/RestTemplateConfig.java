package com.macro.mall.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * @author jack.luo
 * @date 2019/3/1
 */
@Slf4j
@Service
public class RestTemplateConfig {


    @Bean
    public RestTemplate restTemplate() {
        // Do any additional configuration here
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000);
        factory.setReadTimeout(20000);
        return new RestTemplate(factory);
    }

}