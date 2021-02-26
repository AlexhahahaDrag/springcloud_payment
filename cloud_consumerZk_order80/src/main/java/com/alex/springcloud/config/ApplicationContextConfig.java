package com.alex.springcloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @description:
 * @author:      alex
 * @createTime:  2021/2/26 15:01
 * @version:     1.0
 */
@Configuration
public class ApplicationContextConfig {

    @Bean
    //负载均衡
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
