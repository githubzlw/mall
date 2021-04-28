package com.macro.mall.task;

import com.macro.mall.util.ProductUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 插件和API数据进行清洗
 */
@Configuration
@EnableScheduling
public class ProductTask {


    private final ProductUtils productUtils;

    public ProductTask(ProductUtils productUtils) {
        this.productUtils = productUtils;
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    private void cleaningDataScheduled() {
        this.productUtils.cleaningData();
    }
}