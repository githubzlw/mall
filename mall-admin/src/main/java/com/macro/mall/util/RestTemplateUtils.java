package com.macro.mall.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.util
 * @date:2021-05-12
 */
@Component
public class RestTemplateUtils {

    @Autowired
    private RestTemplate restTemplate;

    public String get(String url) {
        return this.restTemplate.getForObject(url, String.class);
    }

    public String post(String url, Map<String, String> param) {
        this.restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        // 设置验签用的数据
        // headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        // headers.add("Authorization", token);
        // 设置content-type,很据需求设置
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 设置请求体
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        String con = JSONObject.toJSONString(param);
        map.add("param", con);
        // 用HttpEntity封装整个请求报文
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = this.restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }


}
