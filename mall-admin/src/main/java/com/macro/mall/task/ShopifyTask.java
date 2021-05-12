package com.macro.mall.task;

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
public class ShopifyTask {

    @Scheduled(cron = "0/5 * * * * ?")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    private void cleaningDataScheduled() {
        // this.productUtils.cleaningData();
    }

}
