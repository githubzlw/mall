package com.macro.mall.onebound.domain;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: 商品详情(taobao和aliexpress)
 * @date:2020/4/26
 */
@Data
public class ItemDetails {

    private String num_iid;
    private String title;
    private String price;
    private String orginal_price;
    /**
     * 库存
     */
    private String num;
    private String detail_url;
    /**
     * 主图
     */
    private String pic_url;
    private String brand;
    private String rootCatId;
    private String cid;
    private String desc;
    /**
     * 橱窗图
     */
    private List<String> item_imgs;

    /**
     * 规格数据
     */
    private JSONObject typeJson;
    /**
     * 规格
     */
    private List<JSONObject> sku;

    /**
     * 属性
     */
    private JSONObject props;

    private String sales;
    private String shop_id;
}
