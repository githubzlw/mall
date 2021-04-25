package com.macro.mall.common.enums;

import lombok.Data;

/**
 * 抓取网站枚举类
 */
public enum ChromeUploadSiteEnum {

    ALIBABA(1,"https://www.alibaba.com/"), ALIEXPRESS(2,"https://www.aliexpress.com/")
    , ESALIEXPRESS(3,"https://es.aliexpress.com/"), AMAZON(4,"https://www.amazon.cn/")
    , WAYFAIR(5,"https://www.wayfair.com/"), EBAY(6,"https://www.ebay.com/")
    , WALMART(7,"https://www.walmart.com/"),ALI1688(8,"https://www.1688.com/");

    private int siteType;
    private String siteDomain;

    ChromeUploadSiteEnum(int siteType,String siteDomain){
        this.siteType = siteType;
        this.siteDomain = siteDomain;
    }

    public int getSiteType(){
        return this.siteType;
    }

    public String getSiteDomain(){
        return this.siteDomain;
    }
}