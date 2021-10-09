package com.macro.mall.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.config.WinitConfig;
import com.macro.mall.domain.WinitParam;
import com.macro.mall.entity.XmsWinitWarehouseStorage;
import com.macro.mall.service.IXmsWinitWarehouseStorageService;
import com.macro.mall.service.impl.XmsWinitWarehouseStorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.util
 * @date:2021-09-27
 */
@Service
@Slf4j
public class WinitUtils {


    @Resource
    private WinitConfig winitConfig;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private IXmsWinitWarehouseStorageService xmsWinitWarehouseStorageService;

    private UrlUtil urlUtil = UrlUtil.getInstance();

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS");


    public int getAndQueryWarehouseStorage(WinitParam winitParam) {
        int total = 0;
        if (winitParam.getPageNum() < 1) {
            winitParam.setPageNum(1);
        }
        if (winitParam.getPageSize() < 1) {
            winitParam.setPageSize(100);
        }
        int currNum = winitParam.getPageNum();
        int currSize = winitParam.getPageSize();

        do {

            try {
                total = this.queryWarehouseStorage(winitParam);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("getAndQueryWarehouseStorage,winitParam[{}],error:", winitParam, e);
            }
            winitParam.setPageNum(++currNum);
        } while (total > (currNum - 1) * currSize);

        return total;
    }

    /**
     * 查询总库存
     *
     * @param winitParam
     */
    public int queryWarehouseStorage(WinitParam winitParam) {

        int total = 0;

        JSONObject warehouseParam = new JSONObject(true);
        warehouseParam.put("action", "queryWarehouseStorage");
        warehouseParam.put("app_key", winitConfig.API_KEY);
        warehouseParam.put("client_id", winitConfig.API_CLIENT_ID);


        warehouseParam.put("format", "json");
        warehouseParam.put("language", "zh_CN");
        warehouseParam.put("platform", winitConfig.API_PLATFORM);

        warehouseParam.put("sign_method", "md5");
        warehouseParam.put("timestamp", LocalDateTime.now().format(formatter));
        warehouseParam.put("version", "3.0");

        JSONObject paramData = new JSONObject(true);
        paramData.put("warehouseID", winitParam.getWarehouseId());
        if (StrUtil.isNotBlank(winitParam.getWarehouseCode())) {
            paramData.put("warehouseCode", winitParam.getWarehouseCode());
        }
        if (StrUtil.isNotBlank(winitParam.getInReturnInventory())) {
            paramData.put("inReturnInventory", winitParam.getInReturnInventory());
        }
        if (StrUtil.isNotBlank(winitParam.getIsActive())) {
            paramData.put("isActive", winitParam.getIsActive());
        }
        if (winitParam.getPageSize() > 0) {
            paramData.put("pageSize", String.valueOf(winitParam.getPageSize()));
        }
        if (winitParam.getPageNum() > 0) {
            paramData.put("pageNum", String.valueOf(winitParam.getPageNum()));
        }

        // , SerializerFeature.SortField
        warehouseParam.put("data", paramData);


        // 获取签名
        warehouseParam.put("sign", WinitSign.genSign(warehouseParam, winitConfig.API_TOKEN));
        warehouseParam.put("client_sign", WinitSign.genClientSign(warehouseParam, winitConfig.API_CLIENT_SIGN));

        JSONObject jsonParam = new JSONObject(true);
        jsonParam.put("action", warehouseParam.getString("action"));
        jsonParam.put("app_key", warehouseParam.getString("app_key"));
        jsonParam.put("client_id", warehouseParam.getString("client_id"));
        jsonParam.put("client_sign", warehouseParam.getString("client_sign"));
        jsonParam.put("data", paramData);

        jsonParam.put("format", warehouseParam.getString("format"));
        jsonParam.put("language", warehouseParam.getString("language"));
        jsonParam.put("platform", warehouseParam.getString("platform"));
        jsonParam.put("sign", warehouseParam.getString("sign"));

        jsonParam.put("sign_method", warehouseParam.getString("sign_method"));
        jsonParam.put("timestamp", warehouseParam.getString("timestamp"));
        jsonParam.put("version", warehouseParam.getString("version"));

        System.err.println("API_URL:" + winitConfig.API_URL);
        System.err.println(warehouseParam);
        //String rs = this.urlUtil.postUrlWithString(winitConfig.API_URL, warehouseParam);

        ResponseEntity<String> entity = this.restTemplate.postForEntity(winitConfig.API_URL, jsonParam, String.class);
        String rs = entity.getBody();
        //System.err.println("rs:" + rs);

        JSONObject jsonObject = JSONObject.parseObject(rs.replaceAll("\r|\n", ""));


        if (null != jsonObject && null != jsonObject.getString("data")) {
            JSONObject tempJson = JSONObject.parseObject(jsonObject.getString("data"));
            if (null != tempJson && null != tempJson.getString("list")) {
                List<XmsWinitWarehouseStorage> storageList = JSONArray.parseArray(tempJson.getString("list"), XmsWinitWarehouseStorage.class);
                total = tempJson.getIntValue("total");
                System.err.println("storageList size:" + storageList.size());
                this.dealStorageList(winitParam.getWarehouseId(), storageList);
            }

        }

        return total;
    }


    private void dealStorageList(String warehouseID, List<XmsWinitWarehouseStorage> storageList) {

        if (CollectionUtil.isNotEmpty(storageList)) {
            QueryWrapper<XmsWinitWarehouseStorage> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(XmsWinitWarehouseStorage::getWarehouseId, warehouseID);
            List<XmsWinitWarehouseStorage> list = this.xmsWinitWarehouseStorageService.list(queryWrapper);

            if (CollectionUtil.isNotEmpty(list)) {
                Map<String, XmsWinitWarehouseStorage> codeSet = new HashMap<>();
                list.forEach(e -> codeSet.put(e.getProductCode(), e));

                list.clear();


                List<XmsWinitWarehouseStorage> insertList = new ArrayList<>();
                List<XmsWinitWarehouseStorage> updateList = new ArrayList<>();

                storageList.forEach(e -> {
                    if (StrUtil.isBlank(e.getWarehouseId())) {
                        e.setWarehouseId(warehouseID);
                    }
                    if (codeSet.containsKey(e.getProductCode())) {
                        e.setId(codeSet.get(e.getProductCode()).getId());
                        e.setUpdateTime(new Date());
                        updateList.add(e);
                    } else {
                        e.setId(null);
                        insertList.add(e);
                    }
                });
                if (CollectionUtil.isNotEmpty(insertList)) {
                    this.xmsWinitWarehouseStorageService.saveBatch(insertList);
                    insertList.clear();
                }
                if (CollectionUtil.isNotEmpty(updateList)) {
                    this.xmsWinitWarehouseStorageService.updateBatchById(updateList);
                    updateList.clear();
                }
                storageList.clear();
                codeSet.clear();
            } else {
                this.xmsWinitWarehouseStorageService.saveBatch(storageList);
            }
        }
    }


}
