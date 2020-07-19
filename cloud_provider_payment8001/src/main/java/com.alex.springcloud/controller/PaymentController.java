package com.alex.springcloud.controller;

import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;
import com.alex.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
//@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @RequestMapping("/payment/get/{id}")
    public CommonResult<Payment> getPayment(@PathVariable(value = "id") Long id) {
        return paymentService.getPayment(id);
    }

    @PostMapping("/payment/create")
    public CommonResult<Integer> createPayment(@RequestBody Payment payment) {
        return paymentService.createPayment(payment);
    }
}
