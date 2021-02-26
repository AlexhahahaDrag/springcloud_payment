package com.alex.springcloud.service;

import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;
import com.alex.springcloud.mapper.PaymentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class PaymentService {

    @Resource
    private PaymentMapper paymentMapper;

    @Value("${server.port}")
    private String port;

    public CommonResult<Payment> getPayment(Long id) {
        Payment payment = paymentMapper.getPaymentById(id);
        if (payment == null) {
            return new CommonResult<>(423, "id为" + id + "的单据不存在");
        } else {
            return new CommonResult<>(200, "查询成功, port" + port, payment);
        }
    }

    public CommonResult<Integer> createPayment(Payment payment) {
        int num =  paymentMapper.createPayment(payment);
        if (num > 0) {
            return new CommonResult<>(200, "创建成功", num);
        } else {
            return new CommonResult<>(423, "创建失败", null);
        }
    }
}
