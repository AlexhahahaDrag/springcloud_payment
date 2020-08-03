package com.alex.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ConsumerFeignOrder80 {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerFeignOrder80.class, args);
    }
}
