package com.macro.mall.util;

import com.alibaba.fastjson.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.util
 * @date:2021-10-08
 */
public class WinitSign {

    private static String ALGORITHM = "MD5";

    public static String genSign(JSONObject warehouseParam, String token) {

        /**
         * 按字段名的字母顺序拼接（包括data中的json字符串也按此规则），将token放在字符串的两端并md5加密，如下所示: 用户签名串 =  token + action + actionValue + app_key + app_keyValue + data + dataValue + format + formatValue + platform + platformValue + sign_method + sign_methodValue + timestamp + timestampValue + version + versionValue + token
         *
         * 对以上拼接后的签名串进行MD5运算，并转换成大写的32位签名。
         *
         * 用户签名sign = toUpperCase(MD5(用户签名串))
         */

        StringBuffer key = new StringBuffer();

        key.append(token);
        key.append("action").append(warehouseParam.getString("action"));
        key.append("app_key").append(warehouseParam.getString("app_key"));
        key.append("data").append(warehouseParam.getString("data"));
        key.append("format").append(warehouseParam.getString("format"));
        key.append("platform").append(warehouseParam.getString("platform"));
        key.append("sign_method").append(warehouseParam.getString("sign_method"));
        key.append("timestamp").append(warehouseParam.getString("timestamp"));
        key.append("version").append(warehouseParam.getString("version"));

        key.append(token);


        StringBuffer buf = new StringBuffer();
        byte[] out;
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(key.toString().getBytes(StandardCharsets.UTF_8));
            out = md.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        for (byte b : out) {
            buf.append(String.format("%02X", b));
        }
        return buf.toString().toUpperCase();

    }


    public static String genClientSign(JSONObject warehouseParam, String client_secret) {

        /**
         * 按字段名的字母顺序拼接（包括data中的json字符串也按此规则），将client_secret放在字符串的两端，如下所示: 应用签名串 = client_secret + action + actionValue + app_key + app_keyValue + data + dataValue + format + formatValue + platform + platformValue + sign_method + sign_methodValue + timestamp + timestampValue + version + versionValue + client_secret
         *
         * 对以上拼接后的签名串进行MD5运算，并转换成大写的32位签名。
         *
         * 应用签名client_sign = toUpperCase(MD5(应用签名串))
         */

        StringBuffer key = new StringBuffer();

        key.append(client_secret);
        key.append("action").append(warehouseParam.getString("action"));
        key.append("app_key").append(warehouseParam.getString("app_key"));
        key.append("data").append(warehouseParam.getString("data"));
        key.append("format").append(warehouseParam.getString("format"));
        key.append("platform").append(warehouseParam.getString("platform"));
        key.append("sign_method").append(warehouseParam.getString("sign_method"));
        key.append("timestamp").append(warehouseParam.getString("timestamp"));
        key.append("version").append(warehouseParam.getString("version"));

        key.append(client_secret);


        StringBuffer buf = new StringBuffer();
        byte[] out;
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(key.toString().getBytes(StandardCharsets.UTF_8));
            out = md.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        for (byte b : out) {
            buf.append(String.format("%02X", b));
        }
        return buf.toString().toUpperCase();

    }

}
