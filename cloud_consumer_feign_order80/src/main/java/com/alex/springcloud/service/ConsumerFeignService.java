package com.alex.springcloud.service;

import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "cloud-provider-payment")
@Component
public interface ConsumerFeignService {

    @RequestMapping("/payment/get/{id}")
    CommonResult<Payment> getPayment(Long id);
}
