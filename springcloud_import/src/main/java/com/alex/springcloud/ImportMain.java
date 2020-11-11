package com.alex.springcloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.alex.springcloud.mapper"})
public class ImportMain {

    public static void main(String[] args) {
        SpringApplication.run(ImportMain.class, args);
    }
}
