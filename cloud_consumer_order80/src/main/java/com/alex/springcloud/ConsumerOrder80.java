package com.alex.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 *description:  consumer order 启动类
 *author:       alex
 *createDate:   2020/7/17 21:40
 *version:      1.0.0
 */
@SpringBootApplication
@EnableEurekaClient
public class ConsumerOrder80 {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerOrder80.class, args);
    }
}
