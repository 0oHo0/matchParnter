package com.duu.matchPartner;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 */
@SpringBootApplication
@MapperScan("com.duu.matchPartner.mapper")
@EnableScheduling
public class matchParnterApplication {

    public static void main(String[] args) {
        SpringApplication.run(matchParnterApplication.class, args);
    }

}
