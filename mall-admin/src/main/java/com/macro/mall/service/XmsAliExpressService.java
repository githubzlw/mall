package com.macro.mall.service;


import com.macro.mall.common.api.CommonResult;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.aliexpress.service
 * @date:2020/3/16
 */
public interface XmsAliExpressService {


    CommonResult getItemByKeyWord(Integer page, String keyword, String start_price, String end_price, String sort, boolean isCache);


    CommonResult getDetails(String pid);
}
