package com.alex.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SentinelMain4008 {

    public static void main(String[] args) {
        SpringApplication.run(SentinelMain4008.class, args);
    }
}
