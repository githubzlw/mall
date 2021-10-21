package com.macro.mall.shopify.config;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.shopify.exception.AccessTokenException;
import com.macro.mall.shopify.exception.ShopifyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jack.luo
 * @date 2019/3/1
 */
@Slf4j
@Service
public class ShopifyRestTemplate {

    private UrlUtil urlUtil = UrlUtil.getInstance();


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ShopifyConfig shopifyConfig;

    @Bean
     public RestTemplate registerTemplate() {
        RestTemplate restTemplate = new RestTemplate(getFactory());
        //这个地方需要配置消息转换器，不然收到消息后转换会出现异常
        //restTemplate.setMessageConverters(getConverts());
        return restTemplate;
    }

    private SimpleClientHttpRequestFactory getFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(180000);
        factory.setReadTimeout(120000);
        return factory;
    }

    private List<HttpMessageConverter<?>> getConverts() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        // String转换器
        StringHttpMessageConverter stringConvert = new StringHttpMessageConverter();
        List<MediaType> stringMediaTypes = new ArrayList<MediaType>() {{
            //添加响应数据格式，不匹配会报401
            add(MediaType.TEXT_PLAIN);
            add(MediaType.TEXT_HTML);
            add(MediaType.APPLICATION_JSON);
        }};
        stringConvert.setSupportedMediaTypes(stringMediaTypes);
        messageConverters.add(stringConvert);
        return messageConverters;
    }


    /**
     * postForEntity
     *
     * @param shopname
     * @param code
     * @return
     */
    public HashMap<String, String> postForEntity(String shopname, String code) {

        Map<String, String> params = new HashMap<>();
        params.put("client_id", shopifyConfig.SHOPIFY_CLIENT_ID);
        params.put("client_secret", shopifyConfig.SHOPIFY_CLIENT_SECRET);
        params.put("code", code);

        ResponseEntity<String> response =
                restTemplate.postForEntity(String.format(shopifyConfig.SHOPIFY_URI_OAUTH, shopname), params, String.class);


        try {
            HashMap<String, String> result = new ObjectMapper().readValue(response.getBody(), HashMap.class);
            return result;
        } catch (IOException e) {
            log.error("postForEntity", e);
            throw new ShopifyException("1001", "postForEntity error");
        }

    }

    /**
     * postForObject
     *
     * @param uri
     * @param token
     * @param json
     * @return
     */
    public String postForObject(String uri, String token, String json) {

        log.info("uri:[{}] token:[{}]  json:[{}]", uri, token, json);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Shopify-Access-Token", token);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        try {
            return restTemplate.postForObject(uri, requestEntity, String.class);
        } catch (Exception e) {
            log.error("postForObject", e);
            if (e.getMessage().contains("Invalid API key or access token") || e.getMessage().contains("401 Unauthorized")) {
                throw new AccessTokenException("1004", "Invalid token");
            } else {
                throw new ShopifyException("1003", "postForObject error");
            }
        }

    }

    /**
     * deleteForObject
     *
     * @param uri
     * @return
     */
    public int deleteForObject(String uri) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            BasicAuthorizationInterceptor basicAuthorizationInterceptor =
                    new BasicAuthorizationInterceptor(shopifyConfig.SHOPIFY_CLIENT_ID, shopifyConfig.SHOPIFY_CLIENT_SECRET);
            restTemplate.getInterceptors().add(basicAuthorizationInterceptor);
            restTemplate.delete(uri);
            return 1;
        } catch (Exception e) {
            log.error("postForObject", e);
            throw e;
        }
    }

    /**
     * exchange
     *
     * @param uri
     * @param token
     * @return
     */
    public String exchange(String uri, String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Shopify-Access-Token", token);
        HttpEntity entity = new HttpEntity(headers);
        String params = null;

        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class, params);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("exchange,uri[{}],token[{}]", uri, token, e);
            if (e.getMessage().contains("Invalid API key or access token") || e.getMessage().contains("401 Unauthorized")) {
                throw new AccessTokenException("1004", "Invalid token");
            } else {
                throw new ShopifyException("1003", "exchange error");
            }
        }
    }

    public String get(String uri, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Shopify-Access-Token", token);
            HttpEntity entity = new HttpEntity(headers);
            String params = null;
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class, params);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Invalid API key or access token") || e.getMessage().contains("401 Unauthorized")) {
                throw new AccessTokenException("1004", "Invalid token");
            } else {
                throw new ShopifyException("1003", "get error");
            }
        }
    }


    /**
     * getObject
     *
     * @param uri
     * @return
     */
    public String getObject(String uri) {

        BasicAuthorizationInterceptor basicAuthorizationInterceptor =
                new BasicAuthorizationInterceptor(shopifyConfig.SHOPIFY_CLIENT_ID, shopifyConfig.SHOPIFY_CLIENT_SECRET);

        restTemplate.getInterceptors().add(basicAuthorizationInterceptor);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String result = restTemplate.getForObject(uri, String.class);
        log.info("result:[{}]", result);
        return result;

    }

    /**
     * postObject
     *
     * @param uri
     * @param json
     * @return
     */
    public String postObject(String uri, String json) {

        BasicAuthorizationInterceptor basicAuthorizationInterceptor =
                new BasicAuthorizationInterceptor(shopifyConfig.SHOPIFY_CLIENT_ID, shopifyConfig.SHOPIFY_CLIENT_SECRET);

        restTemplate.getInterceptors().add(basicAuthorizationInterceptor);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        String result = restTemplate.postForObject(uri, requestEntity, String.class);
        log.info("result:[{}]", result);
        return result;
    }

    /**
     * getShopName
     *
     * @param shop
     * @return
     */
    public String getShopName(String shop) {
        Assert.notNull(shop, "shop must not be null");
        return shop.substring(0, shop.indexOf(".") - 1);
    }


    public String put(String uri, String token, Map<String, Object> param) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Shopify-Access-Token", token);

        System.err.println("---------------------put-----------------");
        System.err.println(uri);
        System.err.println(token);
        System.err.println(JSONObject.toJSONString(param));

        try {
            HttpEntity entity = new HttpEntity(param, headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("put", e);
            if (e.getMessage().contains("Invalid API key or access token") || e.getMessage().contains("401 Unauthorized")) {
                throw new AccessTokenException("1004", "Invalid token");
            } else {
                throw new ShopifyException("1003", "put error");
            }
        }
    }


    public String putJson(String uri, String token, JSONObject param) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Shopify-Access-Token", token);

        System.err.println("---------------------put-----------------");
        System.err.println(uri);
        System.err.println(token);
        System.err.println(JSONObject.toJSONString(param));

        try {
            HttpEntity entity = new HttpEntity(param, headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("put", e);
            if (e.getMessage().contains("Invalid API key or access token") || e.getMessage().contains("401 Unauthorized")) {
                throw new AccessTokenException("1004", "Invalid token");
            } else {
                throw new ShopifyException("1003", "put error");
            }
        }
    }


    public String delete(String uri, String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Shopify-Access-Token", token);

        try {
            System.err.println("url:" + uri);
            System.err.println("token:" + token);

            HttpEntity entity = new HttpEntity(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("put", e);
            if (e.getMessage().contains("Invalid API key or access token") || e.getMessage().contains("401 Unauthorized")) {
                throw new AccessTokenException("1004", "Invalid token");
            } else {
                throw new ShopifyException("1003", "put error");
            }
        }
    }


    public String post(String uri, String token, Map<String, Object> param) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Shopify-Access-Token", token);

        try {
            HttpEntity entity = new HttpEntity(param, headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("post", e);
            if (e.getMessage().contains("Invalid API key or access token") || e.getMessage().contains("401 Unauthorized")) {
                throw new AccessTokenException("1004", "Invalid token");
            } else {
                throw new ShopifyException("1003", "post error");
            }
        }
    }

    public JSONObject postMap(String uri, String token, Map<String, String> params) throws IOException {

       /* HttpHeaders headers = new HttpHeaders();
        headers.set("X-Shopify-Access-Token", token);
        *//*headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));*//*

        HttpEntity<String> entity = new HttpEntity<>("{}", headers);
        Object postForEntity = restTemplate.postForObject(uri, entity, Object.class, jsonObject);
        return JSONObject.parseObject(postForEntity.toString());*/
        return urlUtil.postUrlByToken(uri, token, params);
    }


}
