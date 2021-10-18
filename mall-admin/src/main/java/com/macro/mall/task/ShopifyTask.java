package com.macro.mall.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.UrlUtil;
import com.macro.mall.config.UrlConfig;
import com.macro.mall.domain.ShopifyTaskBean;
import com.macro.mall.model.UmsMember;
import com.macro.mall.service.UmsMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.task
 * @date:2021-10-15
 */
@Configuration
@EnableScheduling
@Slf4j
public class ShopifyTask {

    @Resource
    private UmsMemberService umsMemberService;
    private UrlUtil urlUtil = UrlUtil.getInstance();
    @Resource
    private UrlConfig urlConfig;

    private static final int CICLE_NUM = 50;

    @Scheduled(cron = "0 30 8 * * ?")
    public void syncInfoByShopifyNameTask() {
        this.getAndSyncShopify();
    }

    @Scheduled(cron = "0 30 12 * * ?")
    public void syncInfoByShopifyNameTask1() {
        this.getAndSyncShopify();
    }

    @Scheduled(cron = "0 30 16 * * ?")
    public void syncInfoByShopifyNameTask2() {
        this.getAndSyncShopify();
    }

    @Scheduled(cron = "0 0 21 * * ?")
    public void syncInfoByShopifyNameTask3() {
        this.getAndSyncShopify();
    }


    public void getAndSyncShopifyByList(List<Long> list) {
        List<UmsMember> getList = this.umsMemberService.getByList(list);
        if (CollectionUtil.isNotEmpty(getList)) {
            this.dealList(getList);
        }
    }

    public void getAndSyncShopify() {

        System.err.println("---------------getAndSyncShopify:" + LocalDateTime.now());
        try {

            Long listCount = this.umsMemberService.getListCount();

            if (listCount > 0) {
                int num = (int) (listCount / CICLE_NUM);
                if (listCount % CICLE_NUM > 0) {
                    num++;
                }
                for (int i = 1; i <= num; i++) {
                    List<UmsMember> listByLimit = this.umsMemberService.getListByLimit(CICLE_NUM, i);
                    this.dealList(listByLimit);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ShopifyTask getAndSyncShopify,error:", e);
        }
    }


    private void dealList(List<UmsMember> listByLimit) {

        try {

            String url = urlConfig.getShopifyApiUrl().replace("/shopify", "/shopifyTask") + "/syncInfoByShopifyName";
            System.err.println(url);

            List<ShopifyTaskBean> taskBeanList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(listByLimit)) {
                listByLimit.forEach(e -> {
                    if (StrUtil.isNotBlank(e.getShopifyName())) {
                        ShopifyTaskBean taskBean = new ShopifyTaskBean();
                        taskBean.setMemberId(e.getId());
                        taskBean.setUserName(e.getUsername());
                        taskBean.setShopifyName(e.getShopifyName());
                        taskBeanList.add(taskBean);
                    }
                });
            }

            if (CollectionUtil.isNotEmpty(taskBeanList)) {
                System.err.println(JSONObject.toJSONString(taskBeanList));
                Map<String, String> param = new HashMap<>();
                param.put("listParam", JSONObject.toJSONString(taskBeanList));
                JSONObject jsonObject = this.urlUtil.postURL(url, param);
                CommonResult commonResult = JSONObject.parseObject(jsonObject.toJSONString(), CommonResult.class);
                if (null == commonResult || commonResult.getCode() != 200) {
                    TimeUnit.SECONDS.sleep(3);
                    jsonObject = this.urlUtil.postURL(url, param);
                    commonResult = JSONObject.parseObject(jsonObject.toJSONString(), CommonResult.class);
                }
                if (null == commonResult || commonResult.getCode() != 200) {
                    TimeUnit.SECONDS.sleep(3);
                    jsonObject = this.urlUtil.postURL(url, param);
                    commonResult = JSONObject.parseObject(jsonObject.toJSONString(), CommonResult.class);
                }
                TimeUnit.MINUTES.sleep(3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ShopifyTask dealList,error:", e);
        }
    }
}
