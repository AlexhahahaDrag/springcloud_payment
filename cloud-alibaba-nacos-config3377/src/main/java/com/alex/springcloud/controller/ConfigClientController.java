package com.alex.springcloud.controller;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author:      alex
 * @createTime:  2021/2/26 15:01
 * @version:     1.0
 */
@RestController
@RefreshScope//nacos支持动态刷新
public class ConfigClientController {

    @GetMapping("/config/info")
    public String getConfigInfo() {
        return "111";
    }
}
