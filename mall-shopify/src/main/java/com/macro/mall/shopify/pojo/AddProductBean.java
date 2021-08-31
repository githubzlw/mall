package com.macro.mall.shopify.pojo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.shopify.pojo
 * @date:2021-08-30
 */
@Data
@ApiModel("铺货用到的数据bean")
public class AddProductBean {
    private String shopName;
    private String pid;
    private String published;
    private String skuCodes;
    private String collectionId;
    private String productType;
    private String productTags;
}
