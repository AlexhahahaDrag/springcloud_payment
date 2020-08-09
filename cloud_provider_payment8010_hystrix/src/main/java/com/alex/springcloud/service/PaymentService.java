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
        if (payment == null)
            return new CommonResult<>(423, "id为" + id + "的单据不存在");
        else
            return new CommonResult<>(200, "查询成功, port" + port , payment);
    }

    @HystrixCommand(fallbackMethod = "paymentInfo_TimeOutHandler",
            commandProperties = {@HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="5000")})
    public CommonResult<Payment> getPayment(Long id) {
        logger.info("调用get");
        Payment payment = paymentMapper.getPaymentById(id);
        if (payment == null)
            return new CommonResult<>(423, "id为" + id + "的单据不存在");
        else
            return new CommonResult<>(200, "查询成功, port" + port , payment);
    }

    public CommonResult<Integer> createPayment(Payment payment) {
        int num =  paymentMapper.createPayment(payment);
        if (num > 0)
            return new CommonResult<>(200, "创建成功", num);
        else
            return new CommonResult<>(423, "创建失败", null);
    }

    public CommonResult<Payment> paymentInfo_TimeOutHandler(Long id) {
        return new CommonResult<>(423, "线程池:  " + Thread.currentThread().getName() + "  8009系统繁忙或者运行报错，请稍后再试,id:  "+id+"\t"+"o(╥﹏╥)o");
    }
}
