package com.macro.mall.portal.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.macro.mall.common.api.CommonResult;
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
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    private final OkHttpClient clientLongTime = new OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .build();

    /**
     * call facebookAuth
     * @return
     * @throws IOException
     */
    public String facebookAuth(String code,String url) throws IOException {

        //sample:http://192.168.1.71:18013/facebookAuth?site=IMPORTX&code=1111

        JSONObject jsonObject =
                this.callUrlByGet(url + "/facebookAuth?code=" +code );
        CommonResult commonResult = new Gson().fromJson(jsonObject.toJSONString(), CommonResult.class);
        LOGGER.info("call result:[{}]",commonResult);
        if(commonResult.getCode() !=CommonResult.SUCCESS){
            throw new IllegalStateException("facebookAuth() return value is error. commonResult="+commonResult);
        }

//        FacebookPojo facebookPojo = new FacebookPojo();
        JSONObject data = jsonObject.getJSONObject("data");
//        facebookPojo.setId(data.getString("id"));
//        facebookPojo.setEmail(data.getString("email"));
//        facebookPojo.setName(data.getString("name"));


        return data.getString("email");

    }


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
