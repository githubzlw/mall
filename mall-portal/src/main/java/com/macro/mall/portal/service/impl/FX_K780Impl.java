package com.macro.mall.portal.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.enums.CurrencyEnum;
import com.macro.mall.common.exception.BizErrorCodeEnum;
import com.macro.mall.common.exception.BizException;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.portal.service.ExchangeRateService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class FX_K780Impl implements ExchangeRateService {

    /**
     * {
     * "success": "1",
     * "result": {
     * "status": "ALREADY",
     * "scur": "USD",
     * "tcur": "EUR",
     * "ratenm": "美元/欧元",
     * "rate": "0.9037",
     * "update": "2019-11-18 13:55:00"
     * }
     * }
     */

    private static final String URL = "http://api.k780.com/?app=finance.rate&scur=usd&tcur=%s&appkey=46712&sign=9563c8f536a285c92c8abc940d6c816c&format=jso";

    public static void main(String[] args) throws Exception {
        System.out.println(new FX_K780Impl().getExchangeRate());
    }

    @Override
    public Map<String, BigDecimal> getExchangeRate() throws IOException {
        Map<String, BigDecimal> mapResult = new HashMap<>();

        JSONObject jsonObjectOpt;
        for (CurrencyEnum currencyEnum : CurrencyEnum.values()) {
            jsonObjectOpt = UrlUtil.getInstance().callUrlByGet(String.format(URL, currencyEnum.toString()));
            if (null != jsonObjectOpt && jsonObjectOpt.size() > 0) {
                Assert.isTrue("1".equals(jsonObjectOpt.getString("success")), "query is error");
                JSONObject result = jsonObjectOpt.getJSONObject("result");
                mapResult.put("USD" + currencyEnum.toString(), result.getBigDecimal("rate"));
            } else {
                throw new BizException(BizErrorCodeEnum.BODY_IS_NULL);
            }
        }
        Assert.isTrue(mapResult.size() == 5, "get rate result is not 5");
        return mapResult;

    }


}
