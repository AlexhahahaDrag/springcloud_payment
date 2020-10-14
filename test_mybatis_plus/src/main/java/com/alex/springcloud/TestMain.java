package com.alex.springcloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.alex.springcloud.dao")
public class TestMain {

    public static void main(String[] args) {
        SpringApplication.run(TestMain.class, args);
    }
}
