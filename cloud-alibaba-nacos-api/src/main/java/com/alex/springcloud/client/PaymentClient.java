package com.alex.springcloud.client;

import com.alex.springcloud.common.UrlRest;
import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "cloud-nacos-final-payment")
@RequestMapping(value = UrlRest.PAYMENT_URL)
@Component
public interface PaymentClient {

    @GetMapping(value = UrlRest.PAYMENT_GET_URL)
    CommonResult<Payment> getPaymentById(@PathVariable(value = "id") Long id);
}
