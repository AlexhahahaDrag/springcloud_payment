package com.alex.springcloud.controller;

import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @description:
 * @author:      alex
 * @createTime:  2021/2/26 15:01
 * @version:     1.0
 */
@RestController
@Slf4j
@RequestMapping("/consumer")
public class ConsumerController {

    /**
     * description :负载均衡的时候cloud-provider-payment不能是_
     * author :     alex
     * @param :
     * @return :
     */

    private static final String PROVIDE_URL = "http://CLOUD-PROVIDER-PAYMENT";

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/get/{id}")
    public CommonResult<Payment> getPayment(@PathVariable("id") Long id) {
        String url = PROVIDE_URL + "/get/" + id;
        return restTemplate.getForObject(PROVIDE_URL + "/payment/get/" + id, CommonResult.class);
    }

    @RequestMapping("create")
    public CommonResult<Integer> createPayment(Payment payment) {
        return restTemplate.postForObject(PROVIDE_URL + "/payment/create", payment, CommonResult.class);
    }
}
