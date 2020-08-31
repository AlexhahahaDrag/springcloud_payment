package springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springboot.service.PaymentService;

@RestController("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/getInfo")
    public String getInfo() {
        return paymentService.getInfo();
    }
}
