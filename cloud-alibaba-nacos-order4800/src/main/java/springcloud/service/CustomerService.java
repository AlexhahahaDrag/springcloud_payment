package springcloud.service;

import com.alex.springcloud.client.PaymentClient;
import com.alex.springcloud.entities.CommonResult;
import com.alex.springcloud.entities.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerService {

    @Autowired
    private PaymentClient paymentClient;

    @Value("${server.port}")
    private String port;

    public CommonResult<Payment> getPayment(Long id) {
        return paymentClient.getPaymentById(id);
    }
}
