package com.alex.springcloud.controller;

import com.alex.springcloud.domain.CommonResult;
import com.alex.springcloud.domain.Order;
import com.alex.springcloud.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author:      alex
 * @createTime:  2020/10/9 11:55
 * @version:     1.0
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/create")
    public CommonResult<Integer> createOrder(Order order) {
        int result = orderService.create(order);
        return new CommonResult<Integer>(200, "创建订单成功!!!", result);
    }
}
