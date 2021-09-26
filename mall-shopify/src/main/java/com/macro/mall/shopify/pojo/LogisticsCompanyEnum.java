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


    DHL(1,"DHL","https://www.dhl.com/cn-zh/home.html"),
    CNE(2,"CNE","https://www.cne.com/"),
    FEDEX(2,"FedEx","https://www.cne.com/"),
    COMMON(3,"common","https://www.17track.net/en#nums=");


    private int code;
    private String name;
    private String url;

    LogisticsCompanyEnum(int code, String name, String url) {
        this.code = code;
        this.name = name;
        this.url = url;
    }
}
