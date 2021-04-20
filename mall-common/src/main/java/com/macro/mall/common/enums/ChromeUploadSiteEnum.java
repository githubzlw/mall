package com.macro.mall.common.enums;

import lombok.Data;

/**
 * 抓取网站枚举类
 */
public enum ChromeUploadSiteEnum {

    ALIBABA(1,"alibaba.com"), ALIEXPRESS(2,"aliexpress.com");

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