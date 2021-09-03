package com.macro.mall.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
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

    public final static String MICRO_SERVICE_1688 = "/ali1688-service/";
    public final static String MICRO_SERVICE_PAY = "/pay-service/";

    public final static String MICRO_SERVICE_SHOPIFY = "/shopify-service/";

    /**
     * singleton
     */
    private static UrlUtil singleton = null;

    /**
     * The singleton HTTP client.
     */
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private final OkHttpClient clientLongTime = new OkHttpClient.Builder()
            .connectTimeout(600, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .build();


    /**
     * get调用（有重试机制，默认15次重试，每次1秒）
     * @param url
     * @return
     * @throws IOException
     */
    public JSONObject callUrlByGet(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .get()
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
                        //log.warn("do retry ,times=[{}]",count);
                    }
                    if (null != response && response.isSuccessful()) {
                        break;
                    }
                    if (count > 5) {
                        break;
                    }
                    ++count;
                }
            } catch (InterruptedException e) {
            }
        }

        if (response == null || !response.isSuccessful()) {
            //log.error("url:[{}]", url);
            throw new IOException("call url is not successful");
        }

        return response.body() != null ?
                JSON.parseObject(response.body().string()) : null;
    }

    public JSONObject postFile(File originFile, String paramFileName, String accessUrl) throws IOException {
        String imageType = "image/jpeg";
        if (originFile.getName().endsWith(".png")) {
            imageType = "image/png";
        }
        System.err.println("accessUrl:" + accessUrl);
        RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(paramFileName, originFile.getName(),
                        RequestBody.create(MediaType.parse(imageType), originFile)).build();
        Request request = new Request.Builder().url(accessUrl).post(formBody).build();
        Response response = clientLongTime.newCall(request).execute();
        if (!response.isSuccessful()) {
            LOGGER.error("originFile:[{}],url:[{}],postFile error", originFile, accessUrl);
            throw new IOException("response is not successful");
        }
        return response.body() != null ?
                JSON.parseObject(response.body().string()) : null;
    }

    /**
     * Post调用
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public JSONObject postURL(String url, Map<String, String> params) throws IOException {

        // Create okhttp3 form body builder.
        FormBody.Builder bodyBuilder = new FormBody.Builder();

        // Add form parameters
        params.forEach((k, v) -> {
            if (v != null) bodyBuilder.add(k, v);
        });

        // Build form body.
        FormBody body = bodyBuilder.build();

        // Create a http request object.
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return executeCall(url, request);
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
        //log.info("url:{} tp:{} fileName:{}", url, tp, fileName);

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
            //log.error("response:{}", response);

            throw new IOException("doPostForImgUpload's response is not successful");
        }
        String rs = response.body().string();
        System.err.println(rs);
        return response.body() != null ?
                JSON.parseObject(rs) : null;
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
}
