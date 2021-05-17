package com.macro.mall.shopify.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author jack.luo
 * @date 2019/11/4
 */
@Slf4j
public class UrlUtil {

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

    /**
     * 构造函数
     */
    private UrlUtil() {

    }

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
     * 调用URL（Get）
     *
     * @param url
     * @return
     * @throws IOException
     */
    public Optional<JSONObject> callUrlByGet(String url) {

        log.info("callUrlByGet:{}", url);
        Request request = new Request.Builder().url(url).build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            log.error("IOException", e);
            return Optional.empty();
        }
        if (!response.isSuccessful()) {
            return Optional.empty();
        }
        try {
            return response.body() != null ?
                    Optional.of(JSON.parseObject(response.body().string())) : Optional.empty();
        } catch (IOException e) {
            log.error("IOException", e);
            return Optional.empty();
        }
    }

    /**
     * 调用URL（Get）
     *
     * @param url
     * @return
     * @throws IOException
     */
    public boolean isAccessURL(String url) {

        Request request = new Request.Builder().url(url).build();
        try {
            if (client.newCall(request).execute().isSuccessful()) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }

    }

    /**
     * do post
     *
     * @param url
     * @param tp
     * @param fileName
     * @return
     * @throws IOException
     */
    public JSONObject doPostForImgUpload(String url, String tp, String fileName, String key, String secret) throws IOException {
        log.info("url:{} tp:{} fileName:{}", url, tp, fileName);

        File file = new File(fileName);
        // .addFormDataPart("imgcode", file.getName(),
        //                        RequestBody.create(MediaType.parse("image/jpeg"), file))
        RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("imgcode", file.getName(), body)
                .addFormDataPart("key", key)
                .addFormDataPart("secret", secret)
                .addFormDataPart("api_name", "upload_img")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        // Create a new Call object with put method.
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            log.error("response:{}", response);

            throw new IOException("doPostForImgUpload's response is not successful");
        }
        String rs = response.body().string();
        System.err.println(rs);
        return response.body() != null ?
                JSON.parseObject(rs) : null;
    }

    /**
     * callUrlByPut
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public JSONObject callUrlByPut(String url, Map<String, String> params) throws IOException {

        log.info("callUrlByPut:{},params:{}", url, params);

        FormBody.Builder builder = new FormBody.Builder();
        params.forEach((k, v) -> {
            if (v != null) builder.add(k, v);
        });
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        return executeCall(url, request);

    }

    /**
     * callUrlByPost
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public JSONObject callUrlByPost(String url, Map<String, String> params) throws IOException {

        log.info("callUrlByPost:{},params:{}", url, params);

        FormBody.Builder builder = new FormBody.Builder();
        params.forEach((k, v) -> {
            if (v != null) builder.add(k, v);
        });
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return executeCall(url, request);

    }

    /**
     * call url by retry times
     *
     * @param url
     * @param request
     * @return
     * @throws IOException
     */
    @Nullable
    private JSONObject executeCall(String url, Request request) throws IOException {
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException ioe) {
            //重试15次（每次1秒）
            try {
                int count = 0;
                while (true) {
                    Thread.sleep(1000);
                    try {
                        response = client.newCall(request).execute();
                    } catch (IOException e) {
                        log.warn("do retry ,times=[{}]", count);
                    }
                    if (count > 15) {
                        break;
                    }
                    ++count;
                }
            } catch (InterruptedException e) {
            }
        }

        if (response == null || !response.isSuccessful()) {
            log.error("url:[{}]", url);
            throw new IOException("call url is not successful");
        }

        return response.body() != null ?
                JSON.parseObject(response.body().string()) : null;
    }

    /**
     * addParamToBuilder
     *
     * @param map
     * @return
     */
    private FormBody.Builder addParamToBuilder(Map<String, Object> map) {
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> ite = map.entrySet().iterator();
            for (; ite.hasNext(); ) {
                Map.Entry<String, Object> kv = ite.next();
                builder.add(kv.getKey(), kv.getValue().toString());
            }
        }
        return builder;
    }
}
