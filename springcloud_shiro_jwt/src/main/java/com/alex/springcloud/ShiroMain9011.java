package com.alex.springcloud;

import com.alex.springcloud.properties.SystemProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @description:
 * @author:      alex
 * @createTime:  2020/11/13 14:22
 * @version:     1.0
 */
@SpringBootApplication
@EnableConfigurationProperties(SystemProperties.class)
public class ShiroMain9011 {

    public static void main(String[] args) {
        SpringApplication.run(ShiroMain9011.class, args);
    }
}
