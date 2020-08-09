package com.alex.springcloud.controller;

import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;
import com.alex.springcloud.service.ConsumerHystrixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consumer")
@EnableHystrix
public class ConsumerHystrixController {

    @Autowired
    private ConsumerHystrixService consumerHystrixService;

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public CommonResult<Payment> getPayment(@PathVariable("id") Long id) {
        return consumerHystrixService.getPayment(id);
    }

    @RequestMapping(value = "/getTimeOut/{id}", method = RequestMethod.GET)
    public CommonResult<Payment> getPaymentTimeOut(@PathVariable(value = "id") Long id) {
        return consumerHystrixService.getPaymentTimeOut(id);
    }
}
