package com.macro.mall.shopify.pojo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.shopify.pojo
 * @date:2021-05-19
 */
@ApiModel("物流公司枚举")
@Getter
public enum  LogisticsCompanyEnum {


    DHL(1,"DHL","https://www.dhl.com/global-en/home/search.html"),
    CNE(2,"CNE","https://www.cne.com/"),
    FEDEX(3,"FedEx","https://www.fedex.com/"),
    COMMON(4,"common","https://www.17track.net/en#nums=");


    private int code;
    private String name;
    private String url;

    LogisticsCompanyEnum(int code, String name, String url) {
        this.code = code;
        this.name = name;
        this.url = url;
    }
}
