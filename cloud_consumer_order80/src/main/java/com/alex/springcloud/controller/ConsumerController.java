package com.alex.springcloud.controller;

import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
@RequestMapping("/consumer")
public class ConsumerController {

    private static final String PROVIDE_URL = "http://localhost:8001/payment";

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/get/{id}")
    public CommonResult<Payment> getPayment(@PathVariable("id") Long id) {
        String url = PROVIDE_URL + "/get/" + id;
        return restTemplate.getForObject(PROVIDE_URL + "/get/" + id, CommonResult.class);
    }

    @RequestMapping("create")
    public CommonResult<Integer> createPayment(Payment payment) {
        return restTemplate.postForObject(PROVIDE_URL + "/create", payment, CommonResult.class);
    }
}
