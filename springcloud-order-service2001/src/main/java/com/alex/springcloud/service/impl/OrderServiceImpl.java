package com.alex.springcloud.service.impl;

import com.alex.springcloud.domain.Order;
import com.alex.springcloud.mapper.OrderMapper;
import com.alex.springcloud.service.AccountService;
import com.alex.springcloud.service.OrderService;
import com.alex.springcloud.service.StorageService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private StorageService storageService;

    @Override
    @GlobalTransactional(name = "order_create_info", rollbackFor = Exception.class)
    public int create(Order order) {
        log.info("创建订单！");
        orderMapper.create(order);
        //减少库存
        log.info("减少库存数量");
        storageService.decrease(order.getProductId(), order.getCount());
        //减少账号余额
        log.info("减少账号余额");
        accountService.decrease(order.getUserId(), order.getMoney());
        log.info("修改订单状态为完成");
        orderMapper.update(order.getUserId(), order.getStatus());
        return 1;
    }
}
