package com.macro.mall.onebound.domain;

import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.aliexpress.model
 * @date:2020/3/16
 */
@Data
public class AliExpressItem {

    private String title;
    private String pic_url;
    private String promotion_price;
    private String price;
    private String orginal_price;
    private String sales;
    private String num_iid;
    private String sample_id;
    private String seller_nick;
    private String post_fee;

    private String star;
    private String area;
    private String detail_url;

}
