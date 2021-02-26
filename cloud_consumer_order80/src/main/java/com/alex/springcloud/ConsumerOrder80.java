package com.alex.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @description:
 * @author:      alex
 * @createTime:  2021/2/26 15:01
 * @version:     1.0
 */
@SpringBootApplication
@EnableEurekaClient
public class ConsumerOrder80 {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerOrder80.class, args);
    }
}
