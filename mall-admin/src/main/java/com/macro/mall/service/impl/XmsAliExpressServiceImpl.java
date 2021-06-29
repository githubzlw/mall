package com.macro.mall.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.exception.BizErrorCodeEnum;
import com.macro.mall.common.exception.BizException;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.config.OneBoundConfig;
import com.macro.mall.domain.AliExpressItem;
import com.macro.mall.domain.ItemDetails;
import com.macro.mall.domain.ItemResultPage;
import com.macro.mall.service.XmsAliExpressCacheService;
import com.macro.mall.service.XmsAliExpressService;
import com.macro.mall.util.DataDealUtil;
import com.macro.mall.util.InvalidKeyWord;
import com.macro.mall.util.InvalidPid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.aliexpress.service.impl
 * @date:2020/3/16
 */
@Service
@Slf4j
public class XmsAliExpressServiceImpl implements XmsAliExpressService {
    private final StringRedisTemplate redisTemplate;
    private final XmsAliExpressCacheService cacheService;
    private static final String REDIS_CALL_COUNT = "aliexpress:call:count";
    private static final String REDIS_PID_COUNT = "aliexpress:pid:count";
    private static final String YYYYMMDD = "yyyyMMdd";
    private final OneBoundConfig config;

    /**
     * 获取商品详情
     */
    private final static String URL_ITEM_SEARCH = "https://api.onebound.cn/aliexpress/api_call.php?key=%s&secret=%s&q=%s&api_name=item_search&lang=en&page=%s&cache=no";// &sort=_sale


    private final static String URL_ITEM_DETAILS = "https://api.onebound.cn/aliexpress/api_call.php?api_name=item_get&lang=en&key=%s&secret=%s&num_iid=%s&cache=no";

    @Autowired
    public XmsAliExpressServiceImpl(StringRedisTemplate redisTemplate, XmsAliExpressCacheService cacheService, OneBoundConfig config) {
        this.redisTemplate = redisTemplate;
        this.cacheService = cacheService;
        this.config = config;
    }

    @Override
    public CommonResult getItemByKeyWord(Integer currPage, String keyword, String start_price, String end_price,
                                         String sort, boolean isCache) {
        JSONObject jsonObject = searchResultByKeyWord(currPage, keyword, start_price, end_price, sort, isCache);
        if (jsonObject == null || jsonObject.getJSONObject("items") == null
                || jsonObject.getJSONObject("items").getString("item") == null) {
            return CommonResult.failed("no data");
        } else {
            List<AliExpressItem> aliExpressItems = JSONArray.parseArray(jsonObject.getJSONObject("items").getString("item"), AliExpressItem.class);

            Integer totalNum = jsonObject.getJSONObject("items").getInteger("total_results");
            Integer rsPage = jsonObject.getJSONObject("items").getInteger("page");
            Integer rsPageSize = jsonObject.getJSONObject("items").getInteger("page_size");
            Integer totalPage = totalNum / rsPageSize;
            if (totalNum % rsPageSize > 0) {
                totalPage++;
            }
            List<AliExpressItem> rsList = new ArrayList<>();
            if (aliExpressItems != null && aliExpressItems.size() > 0) {
                aliExpressItems.sort(Comparator.comparing(AliExpressItem::getNum_iid));
                // 价格格式化
                aliExpressItems.forEach(e -> e.setPrice(DataDealUtil.changeAliPrice(e.getPrice())));
                if (aliExpressItems.size() > 28) {
                    rsList = aliExpressItems.stream().limit(28L).collect(Collectors.toList());
                    aliExpressItems.clear();
                } else if (aliExpressItems.size() > 4) {
                    int cicleNum = aliExpressItems.size() / 4;
                    rsList = aliExpressItems.stream().limit(cicleNum * 4L).collect(Collectors.toList());
                    aliExpressItems.clear();
                } else {
                    rsList = aliExpressItems;
                }
            }
            ItemResultPage resultPage = new ItemResultPage(rsList, currPage, rsPageSize, totalPage, totalNum);
            return CommonResult.success(resultPage);
        }
    }

    @Override
    public CommonResult getDetails(String pid) {
        JSONObject itemInfo = getItemInfo(pid, false);
        // 转换成bean
        if (null != itemInfo && itemInfo.containsKey("item")) {
            ItemDetails itemDetail = new ItemDetails();

            JSONObject itemJson = itemInfo.getJSONObject("item");
            itemDetail.setNum_iid(itemJson.getString("num_iid"));
            itemDetail.setTitle(itemJson.getString("title"));
            itemDetail.setPrice(DataDealUtil.dealAliPriceAndChange(itemJson.getString("price")));
            itemDetail.setOrginal_price(DataDealUtil.dealAliPriceAndChange(itemJson.getString("orginal_price")));
            itemDetail.setDetail_url(itemJson.getString("detail_url"));
            itemDetail.setPic_url(itemJson.getString("pic_url"));
            itemDetail.setBrand(itemJson.getString("brand"));
            itemDetail.setRootCatId(itemJson.getString("rootCatId"));
            itemDetail.setCid(itemJson.getString("cid"));
            itemDetail.setDesc(itemJson.getString("desc"));
            itemDetail.setSales(itemJson.getString("sales"));
            itemDetail.setShop_id(itemJson.getString("shop_id"));

            // 橱窗图
            List<String> item_imgs = new ArrayList<>();
            if (itemJson.containsKey("item_imgs")) {
                JSONArray item_imgArr = JSONArray.parseArray(itemJson.getString("item_imgs"));
                if (null != item_imgArr && item_imgArr.size() > 0) {
                    for (int i = 0; i < item_imgArr.size(); i++) {
                        item_imgs.add(item_imgArr.getJSONObject(i).getString("url"));
                    }
                }
            }
            itemDetail.setItem_imgs(item_imgs);

            // 规格数据
            Map<String, String> prop_imgMap = new HashMap<>();
            if (itemJson.containsKey("prop_imgs") && itemJson.getJSONObject("prop_imgs").containsKey("prop_img")) {
                JSONArray prop_imgArr = itemJson.getJSONObject("prop_imgs").getJSONArray("prop_img");
                if (null != prop_imgArr && prop_imgArr.size() > 0) {
                    for (int i = 0; i < prop_imgArr.size(); i++) {
                        prop_imgMap.put(prop_imgArr.getJSONObject(i).getString("properties"),
                                prop_imgArr.getJSONObject(i).getString("url"));
                    }
                    prop_imgArr.clear();
                }
            }

            List<JSONObject> skuList = new ArrayList<>();
            if (itemJson.containsKey("skus") && itemJson.getJSONObject("skus").containsKey("sku")) {
                JSONArray skuArr = itemJson.getJSONObject("skus").getJSONArray("sku");
                if (null != skuArr && skuArr.size() > 0) {
                    for (int i = 0; i < skuArr.size(); i++) {
                        JSONObject skuClJson = new JSONObject();
                        String price = skuArr.getJSONObject(i).getString("price");
                        String orginal_price = skuArr.getJSONObject(i).getString("orginal_price");
                        String properties = skuArr.getJSONObject(i).getString("properties");
                        String properties_name = skuArr.getJSONObject(i).getString("properties_name");
                        String quantity = skuArr.getJSONObject(i).getString("quantity");
                        String sku_id = skuArr.getJSONObject(i).getString("sku_id");
                        String img = "";
                        if (StringUtils.isNotBlank(properties)) {
                            String[] propList = properties.split(";");
                            for (String propCl : propList) {
                                if (prop_imgMap.containsKey(propCl)) {
                                    img = prop_imgMap.get(propCl);
                                    break;
                                }
                            }
                        }
                        skuClJson.put("price", DataDealUtil.dealAliPriceAndChange(price));
                        skuClJson.put("orginal_price", DataDealUtil.dealAliPriceAndChange(orginal_price));
                        skuClJson.put("properties", properties);
                        skuClJson.put("properties_name", properties_name);
                        skuClJson.put("quantity", quantity);
                        skuClJson.put("sku_id", sku_id);
                        skuClJson.put("img", img);
                        skuList.add(skuClJson);
                    }
                    skuArr.clear();
                }
            }
            itemDetail.setSku(skuList);

            // 规格标签展示
            JSONObject typeRsJson = new JSONObject();
            if (itemJson.containsKey("props_list")) {
                JSONObject props_list = itemJson.getJSONObject("props_list");

                props_list.forEach((k, v) -> {
                    JSONObject typeJson = new JSONObject();
                    typeJson.put("id", k);
                    String[] vlist = v.toString().split(":");
                    if (null != vlist && vlist.length == 2) {
                        typeJson.put("label", vlist[0]);

                        typeJson.put("val", vlist[1]);
                        if (prop_imgMap.containsKey(k)) {
                            typeJson.put("img", prop_imgMap.get(k));
                        } else {
                            typeJson.put("img", "");
                        }
                        if (typeRsJson.containsKey(vlist[0])) {
                            typeRsJson.getJSONArray(vlist[0]).add(typeJson);
                        } else {
                            JSONArray array = new JSONArray();
                            array.add(typeJson);
                            typeRsJson.put(vlist[0], array);
                        }
                    }
                });
            }
            itemDetail.setTypeJson(typeRsJson);


            // 解析属性标签
            List<Map> parseArray = JSONArray.parseArray(itemJson.getString("props"), Map.class);
            JSONObject propsMap = new JSONObject();
            if (null != parseArray && parseArray.size() > 0) {
                parseArray.forEach(e -> {
                    propsMap.put(e.get("name").toString(), e.get("value").toString());
                });
                parseArray.clear();
            }
            itemDetail.setProps(propsMap);


            return CommonResult.success(itemDetail);
        } else {
            return CommonResult.failed("no data");
        }
    }

    private JSONObject searchResultByKeyWord(Integer page, String keyword, String start_price, String end_price,
                                             String sort, boolean isCache) {
        /**
         * api.onebound.cn/aliexpress/api_call.php?
         * q=shoe&start_price=&end_price=&page=&cat=&discount_only=&sort=&page_size=&seller_info=&nick=&ppath=&api_name=item_search&lang=zh-CN&key=tel13222738797&secret=20200316
         */
        Objects.requireNonNull(keyword);
        Callable<JSONObject> callable = new Callable<JSONObject>() {

            @Override
            public JSONObject call() {
                return getItemByKeyword(page, keyword, start_price, end_price,
                        sort, isCache);

            }
        };

        Retryer<JSONObject> retryer = RetryerBuilder.<JSONObject>newBuilder()
                .retryIfResult(Predicates.isNull())
                .retryIfExceptionOfType(IllegalStateException.class)
                .withWaitStrategy(WaitStrategies.fixedWait(2000, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();
        try {
            return retryer.call(callable);
        } catch (RetryException | ExecutionException e) {
            throw new BizException(e.getMessage());
        }
    }


    private JSONObject getItemByKeyword(Integer page, String keyword, String start_price, String end_price,
                                        String sort, boolean isCache) {
        Objects.requireNonNull(keyword);
        if (isCache) {
            JSONObject itemFromRedis = this.cacheService.getItemByKeyword(page, keyword, start_price, end_price, sort);
            if (null != itemFromRedis) {
                checkKeyWord(page, keyword, itemFromRedis);
                return itemFromRedis;
            }
        }

        // 组合过滤条件
        StringBuffer sb = new StringBuffer(URL_ITEM_SEARCH);
        if (StringUtils.isNotBlank(start_price)) {
            sb.append("&start_price=" + start_price);
        }
        if (StringUtils.isNotBlank(end_price)) {
            sb.append("&end_price=" + end_price);
        }
        if (StringUtils.isNotBlank(sort)) {
            sb.append("&sort=" + sort);
        }
        System.err.println("url:" + sb.toString());
        JSONObject jsonObject = null;

        try {
            jsonObject = UrlUtil.getInstance().callUrlByGet(String.format(sb.toString(), config.API_KEY, config.API_SECRET, keyword, page));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(BizErrorCodeEnum.BODY_IS_NULL);
        }

        if (null != jsonObject && jsonObject.size() > 0) {
            String strYmd = LocalDate.now().format(DateTimeFormatter.ofPattern(YYYYMMDD));
            this.redisTemplate.opsForHash().increment(REDIS_CALL_COUNT, "keyword_" + strYmd, 1);
            String error = jsonObject.getString("error");
            if (StringUtils.isNotEmpty(error)) {
                if (error.contains("你的授权已经过期")) {
                    throw new BizException(BizErrorCodeEnum.EXPIRE_FAIL);
                } else if (error.contains("超过")) {
                    //TODO
                    throw new BizException(BizErrorCodeEnum.LIMIT_EXCEED_FAIL);
                } else if (error.contains("item-not-found")) {
                    throw new IllegalStateException("item-not-found");
                }
                log.warn("json's error is not empty:[{}]，keyword:[{}]", error, keyword);
                jsonObject = InvalidKeyWord.of(keyword, error);
            }
            this.cacheService.saveItemByKeyword(page, keyword, start_price, end_price, sort, jsonObject);
            checkKeyWord(page, keyword, jsonObject);

            return jsonObject;
        } else {
            throw new BizException(BizErrorCodeEnum.BODY_IS_NULL);
        }

    }

    private void checkKeyWord(Integer page, String keyword, JSONObject jsonObject) {
        Objects.requireNonNull(keyword);
        Objects.requireNonNull(jsonObject);
        JSONObject items = jsonObject.getJSONObject("items");
        if (items != null) {
            if (StringUtils.isEmpty(items.getString("item"))) {
                // this.cacheService.deleteKeyword(page, keyword);
                log.warn("item is null ,keyword:[{}]", keyword);
                throw new BizException(BizErrorCodeEnum.ITEM_IS_NULL);
            }
        }
    }

    @Override
    public JSONObject getItemInfo(String pid, boolean isCache) {
        Objects.requireNonNull(pid);
        if (isCache) {
            JSONObject itemFromRedis = this.cacheService.getItemInfo(pid);
            if (null != itemFromRedis) {
                checkPidInfo(pid, itemFromRedis);
                return itemFromRedis;
            }
        }

        JSONObject jsonObject = null;

        try {
            jsonObject = UrlUtil.getInstance().callUrlByGet(String.format(URL_ITEM_DETAILS, config.API_KEY, config.API_SECRET, pid));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(BizErrorCodeEnum.BODY_IS_NULL);
        }

        if (null != jsonObject && jsonObject.size() > 0) {
            String strYmd = LocalDate.now().format(DateTimeFormatter.ofPattern(YYYYMMDD));
            this.redisTemplate.opsForHash().increment(REDIS_PID_COUNT, "pid_" + strYmd, 1);
            String error = jsonObject.getString("error");
            if (StringUtils.isNotEmpty(error)) {
                if (error.contains("你的授权已经过期")) {
                    throw new BizException(BizErrorCodeEnum.EXPIRE_FAIL);
                } else if (error.contains("超过")) {
                    //TODO
                    throw new BizException(BizErrorCodeEnum.LIMIT_EXCEED_FAIL);
                } else if (error.contains("item-not-found")) {
                    throw new IllegalStateException("item-not-found");
                }
                log.warn("json's error is not empty:[{}]，pid:[{}]", error, pid);
                jsonObject = InvalidPid.of(Long.parseLong(pid), error);
            }

            checkPidInfo(pid, jsonObject);
            this.cacheService.setItemInfo(pid, jsonObject);

            return jsonObject;
        } else {
            throw new BizException(BizErrorCodeEnum.BODY_IS_NULL);
        }

    }


    private void checkPidInfo(String pid, JSONObject jsonObject) {
        Objects.requireNonNull(pid);
        Objects.requireNonNull(jsonObject);
        JSONObject item = jsonObject.getJSONObject("item");
        if (null == item || !item.containsKey("num_iid")) {
            // 保存2小时
            this.cacheService.setItemInfoTime(pid, jsonObject, 2);
            log.warn("itemInfos is null ,pid:[{}]", pid);
            throw new BizException(BizErrorCodeEnum.ITEM_IS_NULL);
        }
    }
}
