package com.alex.springcloud.controller;

import com.alex.springcloud.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *description:  rabbit controller
 *author:       alex
 *createDate:   2020/10/28 22:33
 *version:      1.0.0
 */
@RestController
public class RabbitController {

    @Autowired
    private RabbitService rabbitService;

    @RequestMapping("/sendMessage/{num}")
    public String sendMessage(@PathVariable(value = "num") Integer num) {
        rabbitService.sendMessage(num);
        return "ok";
    }
}
