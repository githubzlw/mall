package com.macro.mall.portal.util;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importExpress.common.util
 * @date:2021-04-01
 */
public enum SiteFlagEnum {
    ALIEXPRESS("www.aliexpress.com", 1, "887766001"), TAOBAO("detail.1688.com/offer", 2, "887766002"), OTHER("otherUrl", 3, "887766003"),IMG_ONLY("imgUrl", 4, "887766004") ;


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
