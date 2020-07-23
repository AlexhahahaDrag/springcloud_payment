package com.alex.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 *description:  consumer order 启动类
 *author:       alex
 *createDate:   2020/7/17 21:40
 *version:      1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ConsumerZkOrder80 {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerZkOrder80.class, args);
    }
}
