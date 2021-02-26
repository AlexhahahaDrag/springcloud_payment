package com.alex.springcloud.service;

import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;
import com.alex.springcloud.mapper.PaymentMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PaymentService {

    private Logger logger = LoggerFactory.getLogger(Payment.class);

    @Autowired
    private PaymentMapper paymentMapper;

    @Value("${server.port}")
    private String port;

    @HystrixCommand(fallbackMethod = "paymentInfo_TimeOutHandler",
            commandProperties = {@HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="5000")})
    public CommonResult<Payment> getPaymentTimeOut(Long id) {
        logger.info("调用getTimeOut");
        try { TimeUnit.MILLISECONDS.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
        Payment payment = paymentMapper.getPaymentById(id);
        if (payment == null) {
            return new CommonResult<>(423, "id为" + id + "的单据不存在");
        } else {
            return new CommonResult<>(200, "查询成功, port" + port , payment);
        }
    }

   @HystrixCommand(fallbackMethod = "paymentInfo_NOT_NAGATIVE", commandProperties = {
            @HystrixProperty(name="circuitBreaker.enabled", value = "true"),                        //是否开启断路器
            @HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value = "10"),          //请求次数，不超过这个次数不会熔断的
            @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value = "10000"),    //时间窗口期
            @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value = "60")})       //失败率达到多少后跳闸
   public CommonResult<Payment> getPayment(Long id) throws Exception{
        if (id < 0) {
            throw new Exception("id不能为负数");
        }
        logger.info("调用get");
        Payment payment = paymentMapper.getPaymentById(id);
        if (payment == null) {
            return new CommonResult<>(423, "id为" + id + "的单据不存在");
        } else {
            return new CommonResult<>(200, "查询成功, port" + port , payment);
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

    public CommonResult<Payment> paymentInfo_TimeOutHandler(Long id) {
        return new CommonResult<>(423, "线程池:  " + Thread.currentThread().getName() + "  8011系统繁忙或者运行报错，请稍后再试,id:  "+id+"\t"+"o(╥﹏╥)o");
    }

    private CommonResult<Payment> paymentInfo_NOT_NAGATIVE(Long id) {
        return new CommonResult<>(423, "线程池:  " + Thread.currentThread().getName() + "  8011系统 id不能为负数，请稍后再试,id:  "+id+"\t"+"o(╥﹏╥)o");
    }
}
