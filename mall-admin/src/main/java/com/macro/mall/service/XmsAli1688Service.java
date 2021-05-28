package com.macro.mall.service;

import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.domain.Ali1688Item;

import java.io.IOException;
import java.util.List;

/**
 * @author jack.luo
 * @date 2019/11/6
 */
public interface XmsAli1688Service {

    /**
     * 1688商品详情查询（单个）
     *
     * @param pid
     * @return
     */
    JSONObject getItem(Long pid, boolean isCache);

    /**
     * 1688商品详情查询（多个）
     *
     * @param pids
     * @return
     */
    List<JSONObject> getItems(Long[] pids, boolean isCache);

    /**
     * 获得店铺商品
     *
     * @param shopid
     * @return
     */
    List<Ali1688Item> getItemsInShop(String shopid);

    /**
     * 上传图片到taobao
     *
     * @param file
     * @return
     */
    String uploadImgToTaobao(String file) throws IOException;

    /**
     * 从缓存中获取图片搜索结果
     * @param md5
     * @return
     */
    JSONObject getImageSearchFromCatch(String md5);

    /**
     * 图片搜索结果保存到缓存中
     * @param md5
     * @param jsonObject
     * @return
     */
    int saveImageSearchFromCatch(String md5, JSONObject jsonObject);

    /**
     * 图片搜索
     *
     * @param imgUrl
     * @return
     */
    JSONObject searchImgFromTaobao(String imgUrl) throws IOException;

    /**
     * 清除redis缓存里面下架商品
     *
     * @return
     */
    int clearNotExistItemInCache();

    /**
     * 清除redis缓存里面所有商品
     * @return
     */
    int clearAllPidInCache();

    /**
     * 清除redis缓存里面所有店铺
     * @return
     */
    int clearAllShopInCache();

    /**
     * 下架商品数量统计
     *
     * @return
     */
    int getNotExistItemInCache();

    /**
     * 设置key的过期时间
     *
     * @param days
     */
    void setItemsExpire(int days);

    /**
     * 获取淘宝商品详情
     *
     * @param pid
     * @return
     */
    CommonResult getDetails(String pid);

    /**
     * 获取阿里巴巴商品详情
     *
     * @param pid 商品ID
     * @return
     */
    JSONObject getAlibabaDetail(Long pid, boolean isCache);

    /**
     * 获取速卖通商品详情
     *
     * @param pid 商品ID
     * @return
     */
    JSONObject getAliexpressDetail(Long pid, boolean isCache);
}
