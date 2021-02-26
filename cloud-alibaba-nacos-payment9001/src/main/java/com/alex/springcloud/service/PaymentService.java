package com.alex.springcloud.service;

import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;

public interface PaymentService {

    /**
     * @param id
     * @description:
     * @author: alex
     * @return: com.alex.springcloud.entities.CommonResult<com.alex.springcloud.entities.Payment>
     */
    CommonResult<Payment> getPayment(Long id);
}
