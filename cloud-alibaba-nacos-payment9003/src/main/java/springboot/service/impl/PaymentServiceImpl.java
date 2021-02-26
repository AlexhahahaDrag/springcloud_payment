package springboot.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springboot.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${server.port}")
    private String port;

    @Override
    public String getInfo() {
        return "alibaba nacos info port : " + port;
    }
}
