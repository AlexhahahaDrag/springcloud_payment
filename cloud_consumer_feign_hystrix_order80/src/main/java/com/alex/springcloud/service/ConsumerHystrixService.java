package com.alex.springcloud.service;

import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;
import com.alex.springcloud.service.impl.ConsumerHystrixFallbackService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@FeignClient(value = "CLOUD-PROVIDER-PAYMENT-HYSTRIX", fallback = ConsumerHystrixFallbackService.class)
public interface ConsumerHystrixService {

    @RequestMapping(value = "/payment/get/{id}", method = RequestMethod.GET)
    CommonResult<Payment> getPayment(@PathVariable(value = "id") Long id) ;

    @RequestMapping(value = "/payment/getTimeOut/{id}", method = RequestMethod.GET)
    CommonResult<Payment> getPaymentTimeOut(@PathVariable(value = "id") Long id);
}
