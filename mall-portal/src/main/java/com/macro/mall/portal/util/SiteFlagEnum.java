package com.macro.mall.portal.util;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importExpress.common.util
 * @date:2021-04-01
 */
public enum SiteFlagEnum {
    ALIBABA("https://www.alibaba.com", 1, "88800001"),
    ALIEXPRESS("https://www.aliexpress.com", 2, "88800002"),
    ESALIEXPRESS("https://es.aliexpress.com", 3, "88800003"),
    AMAZON("https://www.amazon.cn", 4, "88800005"),
    WAYFAIR("https://www.wayfair.com", 5, "88800005"),
    EBAY("https://www.ebay.com", 6, "88800006"),
    WALMART("https://www.walmart.com", 7, "88800007"),
    ALI1688("https://detail.1688.com", 8, "88800008"),

    IMG_ONLY("imgUrl", 9, "88800009"),
    OTHER("otherUrl", 10, "88800010"),
    SHOPIFY("shopifyUrl", 11, "888000011");


    private String url;
    private int flag;
    private String catid;

    public String getUrl() {
        return url;
    }

    public int getFlag() {
        return flag;
    }

    public String getCatid() {
        return catid;
    }

    SiteFlagEnum(String url, int flag, String catid) {
        this.url = url;
        this.flag = flag;
        this.catid = catid;
    }
}
