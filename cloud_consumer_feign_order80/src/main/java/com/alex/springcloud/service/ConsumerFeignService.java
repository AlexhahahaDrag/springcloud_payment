package com.alex.springcloud.service;

import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:
 * @author:      alex
 * @createTime:  2021/2/26 15:01
 * @version:     1.0
 */
@FeignClient(value = "cloud-provider-payment")
@Component
public interface ConsumerFeignService {

    /**
     * @param id
     * @description:
     * @author: alex
     * @return: com.alex.springcloud.entities.CommonResult<com.alex.springcloud.entities.Payment>
     */
    @RequestMapping("/payment/get/{id}")
    CommonResult<Payment> getPayment(Long id);
}
