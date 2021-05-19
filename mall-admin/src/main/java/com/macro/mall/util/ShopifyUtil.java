package com.macro.mall.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author jack.luo
 * @date 2019/3/1
 */
@Slf4j
@Service
public class ShopifyUtil {


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Config config;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Do any additional configuration here
        return builder.build();
    }

    /**
     * postForObject
     * @param uri
     * @param token
     * @param json
     * @return
     */
    public String postForObject(String uri, String token, String json) {

        log.info("uri:[{}] token:[{}]  json:[{}]",uri,token,json);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Shopify-Access-Token", token);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        try {
            return restTemplate.postForObject(uri, requestEntity, String.class);
        } catch (Exception e) {
            log.error("postForObject",e);
            throw e;
        }

    }




}
