package com.alex.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 *description:
 *author:       alex
 *createDate:   2020/8/10 21:30
 *version:      1.0.0
 */
@SpringBootApplication
@EnableHystrixDashboard
public class PaymentMainDashboard9001 {

    public static void main(String[] args) {
        SpringApplication.run(PaymentMainDashboard9001.class, args);
    }
}
