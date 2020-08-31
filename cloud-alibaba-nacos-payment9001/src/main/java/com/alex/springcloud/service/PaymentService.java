package com.alex.springcloud.service;

import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;

public interface PaymentService {

    CommonResult<Payment> getPayment(Long id);
}
