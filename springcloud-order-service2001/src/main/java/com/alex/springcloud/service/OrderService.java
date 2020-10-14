package com.alex.springcloud.service;

import com.alex.springcloud.domain.Order;

/**
 * @description: 订单service
 * @author:      alex
 * @createTime:  2020/10/9 11:53
 * @version:     1.0
 */
public interface OrderService {

    int create(Order order);
}
