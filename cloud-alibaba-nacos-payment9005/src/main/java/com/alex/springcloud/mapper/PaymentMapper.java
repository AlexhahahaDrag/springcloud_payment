package com.alex.springcloud.mapper;

import com.alex.springcloud.entities.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {

    Payment getPaymentById(Long id);

    int createPayment(Payment payment);
}
