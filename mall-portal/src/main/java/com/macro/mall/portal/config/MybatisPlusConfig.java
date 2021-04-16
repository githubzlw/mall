package com.macro.mall.portal.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jack.luo
 * @date 2021/4/16
 */
@Configuration
//@MapperScan("com.macro.mall.mapper.*")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor innerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        //不受数量限制
        innerInterceptor.setMaxLimit(-1L);
        interceptor.addInnerInterceptor(innerInterceptor);
        return interceptor;
    }

}
