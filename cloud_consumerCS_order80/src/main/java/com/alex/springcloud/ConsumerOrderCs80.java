package com.alex.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @description:
 * @author:      alex
 * @createTime:  2021/2/26 15:01
 * @version:     1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ConsumerOrderCs80 {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerOrderCs80.class, args);
    }
}
