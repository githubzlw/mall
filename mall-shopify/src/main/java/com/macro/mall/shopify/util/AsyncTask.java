package com.macro.mall.shopify.util;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.entity.XmsShopifyPidImg;
import com.macro.mall.entity.XmsShopifyPidImgError;
import com.macro.mall.shopify.config.ShopifyConfig;
import com.macro.mall.shopify.config.ShopifyRestTemplate;
import com.macro.mall.shopify.service.IXmsShopifyAuthService;
import com.macro.mall.shopify.service.IXmsShopifyPidImgErrorService;
import com.macro.mall.shopify.service.IXmsShopifyPidImgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.shopify.util
 * @date:2021-09-10
 */
@Slf4j
@Component
public class AsyncTask {

    @Resource
    private IXmsShopifyPidImgService xmsShopifyPidImgService;
    @Resource
    private ShopifyRestTemplate shopifyRestTemplate;
    @Resource
    private ShopifyConfig shopifyConfig;
    @Resource
    private IXmsShopifyPidImgErrorService xmsShopifyPidImgErrorService;
    @Resource
    private IXmsShopifyAuthService xmsShopifyAuthService;

    /**
     * 读取商品的图片信息
     *
     * @param pidSet
     * @param shopifyName
     */
    @Async("taskExecutor")
    public void getShopifyImgByList(Set<Long> pidSet, String shopifyName) {
        if (CollectionUtil.isNotEmpty(pidSet)) {
            String accessToken = this.xmsShopifyAuthService.getShopifyToken(shopifyName);
            // 过滤错误的PID读取数据

            QueryWrapper<XmsShopifyPidImgError> imgErrorQueryWrapper = new QueryWrapper<>();
            imgErrorQueryWrapper.lambda().in(XmsShopifyPidImgError::getShopifyPid, Arrays.asList(pidSet.toArray()))
                    .eq(XmsShopifyPidImgError::getShopifyName, shopifyName);
            List<XmsShopifyPidImgError> errorList = this.xmsShopifyPidImgErrorService.list(imgErrorQueryWrapper);

            Set<Long> delaySet = new HashSet<>();
            if (CollectionUtil.isNotEmpty(errorList)) {
                errorList.forEach(e -> delaySet.add(Long.parseLong(e.getShopifyPid())));
                errorList.clear();
            }
            Set<Long> normalSet = pidSet.stream().filter(e -> !delaySet.contains(e)).collect(Collectors.toSet());
            pidSet.clear();

            if (normalSet.size() > 0) {
                Set<Long> filterSet = new HashSet<>();
                QueryWrapper<XmsShopifyPidImg> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().in(XmsShopifyPidImg::getShopifyPid, Arrays.asList(normalSet.toArray()))
                        .eq(XmsShopifyPidImg::getShopifyName, shopifyName);
                List<XmsShopifyPidImg> list = this.xmsShopifyPidImgService.list(queryWrapper);
                if (CollectionUtil.isNotEmpty(list)) {
                    list.forEach(e -> {
                        if (!normalSet.contains(Long.parseLong(e.getShopifyPid()))) {
                            filterSet.add(Long.parseLong(e.getShopifyPid()));
                        }
                    });
                    list.clear();
                } else {
                    filterSet.addAll(normalSet);
                }

                Set<Long> longSet = this.tryGetNewPidImg(accessToken, shopifyName, filterSet);

                if (longSet.size() > 0) {
                    delaySet.addAll(longSet);
                }
            }

            this.tryGetErrorPidImg(accessToken, shopifyName, delaySet);
        }
    }

    /**
     * 优先处理新过来的商品PID
     *
     * @param accessToken
     * @param shopifyName
     * @param filterSet
     * @return
     */
    private Set<Long> tryGetNewPidImg(String accessToken, String shopifyName, Set<Long> filterSet) {
        Set<Long> delaySet = new HashSet<>();

        List<String> sucList = new ArrayList<>();
        if (filterSet.size() > 0) {
            // 过滤后，挨个读取图片信息
            filterSet.forEach(e -> {
                boolean b = this.loopGainImg(e, accessToken, shopifyName);
                if (b) {
                    sucList.add(String.valueOf(e));
                } else {
                    delaySet.add(e);
                }
            });
            filterSet.clear();
        }
        // 成功的删除error数据
        this.deleteSuccessImg(shopifyName, sucList);
        return delaySet;
    }

    /**
     * 处理以前错误和现在错误的PID
     *
     * @param accessToken
     * @param shopifyName
     * @param delaySet
     */
    private void tryGetErrorPidImg(String accessToken, String shopifyName, Set<Long> delaySet) {
        List<String> sucList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        if (delaySet.size() > 0) {
            delaySet.forEach(e -> {
                boolean b = this.singleGetErrorImgInfo(e, accessToken, shopifyName);
                if (b) {
                    sucList.add(String.valueOf(e));
                } else {
                    errorList.add(String.valueOf(e));
                }
            });
            this.deleteSuccessImg(shopifyName, sucList);
            if (CollectionUtil.isNotEmpty(errorList)) {
                this.saveErrorImg(shopifyName, errorList);
                errorList.clear();
            }
        }
    }

    /**
     * 重试
     *
     * @param pid
     * @param accessToken
     * @param shopifyName
     * @return
     */
    private boolean singleGetErrorImgInfo(Long pid, String accessToken, String shopifyName) {
        boolean b = this.loopGainImg(pid, accessToken, shopifyName);
        int count = 0;
        while (!b && count < 2) {
            count++;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            b = this.loopGainImg(pid, accessToken, shopifyName);
        }
        return b;
    }


    private boolean loopGainImg(Long pid, String accessToken, String shopifyName) {
        try {

            String url = String.format(this.shopifyConfig.SHOPIFY_URI_PRODUCTS_IMGS, shopifyName, pid);

            String json = this.shopifyRestTemplate.get(url, accessToken);
            if (null != json) {
                JSONObject jsonObject = JSONObject.parseObject(json);
                XmsShopifyPidImg pidImg = new XmsShopifyPidImg();
                JSONArray images = jsonObject.getJSONArray("images");
                if (null != images && images.size() > 0) {
                    pidImg.setShopifyPid(String.valueOf(pid));
                    pidImg.setShopifyName(shopifyName);
                    pidImg.setImg(images.getJSONObject(0).getString("src"));
                    pidImg.setImgInfo(images.toJSONString());
                    pidImg.setCreateTime(new Date());
                    this.xmsShopifyPidImgService.save(pidImg);
                    return true;
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("singleGetImgInfo,shopifyName[{}],pid[{}],error:", shopifyName, pid, e);
        }
        return false;
    }


    private void deleteSuccessImg(String shopifyName, List<String> sucList) {
        try {
            if (sucList.size() > 0) {
                // 成功的删除error数据
                QueryWrapper<XmsShopifyPidImgError> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(XmsShopifyPidImgError::getShopifyName, shopifyName)
                        .in(XmsShopifyPidImgError::getShopifyPid, sucList);
                this.xmsShopifyPidImgErrorService.remove(queryWrapper);
                sucList.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveErrorImg(String shopifyName, List<String> errorList) {
        synchronized (shopifyName) {
            try {

                QueryWrapper<XmsShopifyPidImgError> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(XmsShopifyPidImgError::getShopifyName, shopifyName)
                        .in(XmsShopifyPidImgError::getShopifyPid, errorList);
                List<XmsShopifyPidImgError> list = this.xmsShopifyPidImgErrorService.list(queryWrapper);
                Set<String> collect = new HashSet<>();
                if (CollectionUtil.isNotEmpty(list)) {
                    list.forEach(e -> collect.add(e.getShopifyPid()));

                    list = list.stream().filter(e -> !errorList.contains(e)).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(list)) {
                        list.forEach(e -> e.setTotal(null == e.getTotal() ? 1 : e.getTotal() + 1) );

                        this.xmsShopifyPidImgErrorService.updateBatchById(list);
                        list.clear();
                    }

                }

                if (errorList.size() > 0) {
                    List<XmsShopifyPidImgError> imgErrorList = new ArrayList<>();
                    errorList.stream().filter(e -> !collect.contains(e)).forEach(e -> {
                        XmsShopifyPidImgError temp = new XmsShopifyPidImgError();
                        temp.setShopifyName(shopifyName);
                        temp.setShopifyPid(e);
                        temp.setTotal(1L);
                        imgErrorList.add(temp);
                    });
                    if (CollectionUtil.isNotEmpty(imgErrorList)) {
                        this.xmsShopifyPidImgErrorService.saveBatch(imgErrorList);
                    }


                    errorList.clear();
                }
                collect.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
