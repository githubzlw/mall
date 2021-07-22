package com.macro.mall.common.enums;

import lombok.Data;

/**
 * 抓取网站枚举类
 */
public enum ChromeUploadSiteEnum {

    ALIBABA(1,"https://www.alibaba.com/","alibaba"),
    ALIEXPRESS(2,"https://www.aliexpress.com/","aliexpress"),
    // ESALIEXPRESS(3,"https://es.aliexpress.com/","aliexpress"),
    AMAZON(4,"https://www.amazon.com/","amazon"),
    WAYFAIR(5,"https://www.wayfair.com/","wayfair"),
    EBAY(6,"https://www.ebay.com/","ebay"),
    WALMART(7,"https://www.walmart.com/","walmart"),
    ALI1688(8,"https://detail.1688.com/","detail");

    private int siteType;
    private String siteDomain;
    private String siteName;

    ChromeUploadSiteEnum(int siteType,String siteDomain, String siteName){
        this.siteType = siteType;
        this.siteDomain = siteDomain;
        this.siteName = siteName;
    }

    public int getSiteType(){
        return this.siteType;
    }

    public String getSiteDomain(){
        return this.siteDomain;
    }

    public String getSiteName() {
        return siteName;
    }
}