package com.alex.springcloud.mapper;

import com.alex.springcloud.entities.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {

    /**
     * @param id
     * @description:
     * @author: alex
     * @return: com.alex.springcloud.entities.Payment
     */
    Payment getPaymentById(Long id);

    /**
     * @param payment
     * @description:
     * @author: alex
     * @return: int
     */
    int createPayment(Payment payment);
}
