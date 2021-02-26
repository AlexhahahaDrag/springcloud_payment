package com.alex.springcloud.service.impl;

import com.alex.springcloud.mapper.PaymentMapper;
import com.alex.springcloud.service.PaymentService;
import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Value("${server.port}")
    private String port;

    @Override
    public CommonResult<Payment> getPayment(Long id) {
        Payment payment = paymentMapper.getPaymentById(id);
        if (payment == null) {
            return new CommonResult<>(423, "id为" + id + "的单据不存在");
        } else {
            return new CommonResult<>(200, "查询成功, port" + port , payment);
        }
    }
}
