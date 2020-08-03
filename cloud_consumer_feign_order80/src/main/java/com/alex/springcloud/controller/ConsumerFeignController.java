package com.alex.springcloud.controller;

import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;
import com.alex.springcloud.service.ConsumerFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consumer")
public class ConsumerFeignController {

    @Autowired
    private ConsumerFeignService consumerFeignService;

    @RequestMapping("/get/{id}")
    public CommonResult<Payment> getPayment(@PathVariable("id") Long id) {
        return consumerFeignService.getPayment(id);
    }
}
