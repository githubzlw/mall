package com.macro.mall.portal.util;

import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.util
 * @date:2021-04-15
 */
@Component
@Slf4j
public class ExchangeRateUtils {


    @Value("${localService.exchangeRateApi.url}")
    private String exchangeRateUrl;

    private double usdToCnyRate = 0;// 美元对人名币汇率

    public double getUsdToCnyRate() {

        if (this.usdToCnyRate <= 5) {
            synchronized (ExchangeRateUtils.class) {
                getByLocalUrl();
            }
        }
        if (this.usdToCnyRate <= 5) {
            this.usdToCnyRate = 6.5;
        }
        return this.usdToCnyRate;
    }

    /**
     * 根据本地API接口调用汇率数据
     *
     * @return
     */
    private void getByLocalUrl() {
        // {"USDEUR":0.902959,"USDGBP":0.772016,"USDCNY":7.0261,"USDAUD":1.471267,"USDCAD":1.32071}

        try {
            Map<String, Double> map = new HashMap<String, Double>(10);
            JSONObject rateJson = UrlUtil.getInstance().callUrlByGet(exchangeRateUrl);
            if (rateJson.getDouble("USDEUR") != 0) {
                map.put("USDEUR", rateJson.getDouble("USDEUR"));
            }
            if (rateJson.getDouble("USDGBP") != 0) {
                map.put("USDGBP", rateJson.getDouble("USDGBP"));
            }
            if (rateJson.getDouble("USDAUD") != 0) {
                map.put("USDAUD", rateJson.getDouble("USDAUD"));
            }
            if (rateJson.getDouble("USDCAD") != 0) {
                map.put("USDCAD", rateJson.getDouble("USDCAD"));
            }
            if (rateJson.getDouble("USDCNY") != 0) {
                map.put("USDCNY", rateJson.getDouble("USDCNY"));
                this.usdToCnyRate = rateJson.getDouble("USDCNY");
            }
            //return map;
        } catch (IOException e) {
            log.error("getByLocalUrl", e);
            // throw new RuntimeException("call exchangeRate error");
        }
    }

}
