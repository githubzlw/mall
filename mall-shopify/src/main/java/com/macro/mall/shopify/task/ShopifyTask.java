package com.macro.mall.shopify.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: shopify的订单相关数据
 * @date:2021-05-12
 */
@Configuration
@EnableScheduling
@Slf4j
public class ShopifyTask {

    @Scheduled(cron = "0 0 0/6 * * ?")
    public void getOrdersByShopifyNameTask() {}

}
