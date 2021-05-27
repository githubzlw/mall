package com.macro.mall.onebound;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {
DataSourceAutoConfiguration.class,
DataSourceTransactionManagerAutoConfiguration.class,
DruidDataSourceAutoConfigure.class ,
HibernateJpaAutoConfiguration.class})
public class MallOneboundApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallOneboundApplication.class, args);
    }

}
