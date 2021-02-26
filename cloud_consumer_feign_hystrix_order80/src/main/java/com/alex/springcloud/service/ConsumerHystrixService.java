package com.alex.springcloud.service;

import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;
import com.alex.springcloud.service.impl.ConsumerHystrixFallbackServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @description:
 * @author:      alex
 * @createTime:  2021/2/26 15:01
 * @version:     1.0
 */
@Component
@FeignClient(value = "CLOUD-PROVIDER-PAYMENT-HYSTRIX", fallback = ConsumerHystrixFallbackServiceImpl.class)
public interface ConsumerHystrixService {

    /**
     * @param id
     * @description:
     * @author: alex
     * @return: com.alex.springcloud.entities.CommonResult<com.alex.springcloud.entities.Payment>
     */
    @RequestMapping(value = "/payment/get/{id}", method = RequestMethod.GET)
    CommonResult<Payment> getPayment(@PathVariable(value = "id") Long id) ;

    /**
     * @param id
     * @description:
     * @author: alex
     * @return: com.alex.springcloud.entities.CommonResult<com.alex.springcloud.entities.Payment>
     */
    @RequestMapping(value = "/payment/getTimeOut/{id}", method = RequestMethod.GET)
    CommonResult<Payment> getPaymentTimeOut(@PathVariable(value = "id") Long id);
}
