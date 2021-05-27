package com.macro.mall.onebound.util;

import com.alibaba.fastjson.JSONObject;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.aliexpress.service.util
 * @date:2020/3/16
 */
public class InvalidKeyWord {

    private InvalidKeyWord(){

    }

    /**
     * return invalid keyWord's json object
     *
     * @param keyWord
     * @param resason
     * @return
     */
    public static @NonNull JSONObject of(String keyWord, String resason) {
        Objects.requireNonNull(keyWord);
        Objects.requireNonNull(resason);
        JSONObject jsonObject = new JSONObject();
        final String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        jsonObject.put("secache_date", now);
        jsonObject.put("server_time", now);
        jsonObject.put("reason", resason);
        jsonObject.put("keyWord", keyWord);
        return jsonObject;
    }
}
