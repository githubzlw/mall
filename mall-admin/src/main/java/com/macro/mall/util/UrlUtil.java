package com.macro.mall.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CharMatcher;
import com.google.gson.Gson;
import com.macro.mall.common.api.CommonResult;
import okhttp3.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zlw
 * @date 2021/4/20
 */
@RequestMapping("/UrlUtil")
public class UrlUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlUtil.class);

    public final static String ZUUL_SHOPIFY = "zuul.url/shopify-service/";

    public static final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    /**
     * singleton
     */
    private static UrlUtil singleton = null;

    /**
     * The singleton HTTP client.
     */
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    private final OkHttpClient clientLongTime = new OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .build();

    /**
     * getInstance
     *
     * @return
     */
    public static UrlUtil getInstance() {

        if (singleton == null) {
            synchronized (UrlUtil.class) {
                if (singleton == null) {
                    singleton = new UrlUtil();
                }
            }
        }
        return singleton;
    }
    /**
     * Post调用
     *
     * @param url
     * @return
     * @throws IOException
     */
    public JSONObject callUrlByPost(String url, Object param) throws IOException {
        String param_ = JSONObject.toJSONString(param);
        RequestBody requestBody = RequestBody.create(mediaType, param_);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        return executeCall(url, request);
    }

    /**
     * call url by retry times
     * @param url
     * @param request
     * @return
     * @throws IOException
     */
    @Nullable
    private JSONObject executeCall(String url, Request request) throws IOException {
        Response response =null;
        try{
            response = client.newCall(request).execute();
        }catch(IOException ioe){
            //重试15次（每次1秒）
            try {
                int count=0;
                while(true){
                    Thread.sleep(1000);
                    try {
                        response = client.newCall(request).execute();
                    } catch (IOException e) {
                        //log.warn("do retry ,times=[{}]",count);
                    }
                    if(count>15){
                        break;
                    }
                    ++count;
                }
            } catch (InterruptedException e) {
            }
        }

        if (response==null || !response.isSuccessful()) {
            //log.error("url:[{}]", url);
            throw new IOException("call url is not successful");
        }

        return response.body() != null ?
                JSON.parseObject(response.body().string()) : null;
    }
}
