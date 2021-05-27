package com.macro.mall.onebound.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OneBoundConfig {

    @Value("${ali1688api.API_HOST}")
    public String API_HOST;


    @Value("${ali1688api.API_KEY}")
    public String API_KEY;

    @Value("${ali1688api.API_SECRET}")
    public String API_SECRET;


    @Value("${ali1688api.dates.pid}")
    public String datesPid;

    @Value("${ali1688api.dates.shop}")
    public String datesShop;

    @Value("${ali1688api.shop.minSales}")
    public int minSales;


}