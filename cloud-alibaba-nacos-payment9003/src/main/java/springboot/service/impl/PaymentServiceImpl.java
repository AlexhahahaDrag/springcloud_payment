package springboot.service.impl;

import com.alex.springboot.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("server.port")
    private String port;

    public String getInfo() {
        return "alibaba nacos info port : " + port;
    }
}
