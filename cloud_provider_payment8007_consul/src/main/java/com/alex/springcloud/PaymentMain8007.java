package com.alex.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PaymentMain8007 {

    public static void main(String[] args) {
        SpringApplication.run(PaymentMain8007.class, args);
    }
}
