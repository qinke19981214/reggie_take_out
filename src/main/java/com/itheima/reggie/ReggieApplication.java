package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@MapperScan("com.itheima.reggie.mapper")
@ServletComponentScan  //扫描类似过滤器组件
@EnableTransactionManagement   //开启事务
public class ReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class);
        log.info("项目启动");

    }

}
