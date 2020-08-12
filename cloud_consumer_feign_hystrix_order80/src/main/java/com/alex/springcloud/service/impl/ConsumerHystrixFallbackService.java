package com.alex.springcloud.service.impl;

import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;
import com.alex.springcloud.service.ConsumerHystrixService;
import org.springframework.stereotype.Component;

@Component

public class ConsumerHystrixFallbackService implements ConsumerHystrixService {

    @Override
    public CommonResult<Payment> getPayment(Long id) {
        return new CommonResult<>(423, "线程池:  " + Thread.currentThread().getName() + "  80系统繁忙或者运行报错，请稍后再试,id:  "+id+"\t"+"o(╥﹏╥)o");
    }

    @Override
    public CommonResult<Payment> getPaymentTimeOut(Long id) {
        return new CommonResult<>(423, "线程池:  " + Thread.currentThread().getName() + "  80timeOut系统繁忙或者运行报错，请稍后再试,id:  "+id+"\t"+"o(╥﹏╥)o");
    }
}
