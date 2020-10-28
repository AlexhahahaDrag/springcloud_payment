package com.alex.springcloud;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class RabbitMQMain9010 {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMQMain9010.class, args);
    }
}
