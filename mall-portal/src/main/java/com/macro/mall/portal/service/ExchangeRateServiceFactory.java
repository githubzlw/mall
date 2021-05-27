package com.macro.mall.portal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author jack.luo
 * @date 2019/11/18
 */
@Service
@Slf4j
public class ExchangeRateServiceFactory {

    @Resource(name = "FX_K780Impl")
    private ExchangeRateService fX_K780Impl;

    @Resource(name = "FX_OERImpl")
    private ExchangeRateService fX_OERImpl;


    public Map<String, BigDecimal> getExchangeRate() throws IOException {

        try {
            Map<String, BigDecimal> exchangeRate = fX_OERImpl.getExchangeRate();
            return exchangeRate;
        } catch (IOException | IllegalArgumentException ex) {
            log.error("do fX_OERImpl() error,begin switch K780", ex);

            try {
                Map<String, BigDecimal> exchangeRate = fX_K780Impl.getExchangeRate();
                return exchangeRate;
            } catch (IOException | IllegalArgumentException ex2) {
                log.error("getExchangeRate", ex2);
                throw new IOException("two ways is all error");
            }
        }

    }


}
