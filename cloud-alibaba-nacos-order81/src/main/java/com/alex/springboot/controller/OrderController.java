package com.alex.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController("/customer")
public class OrderController {

    private String url = "localhost:8848";

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/getInfo")
    public String getInfo() {
        return restTemplate.getForObject(url + "/payment/getInfo", String.class);
    }
}
