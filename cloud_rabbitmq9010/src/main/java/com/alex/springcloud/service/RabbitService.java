package com.alex.springcloud.service;

import com.alex.springcloud.bean.Customer;
import com.alex.springcloud.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *description:  rabbit 信息发送类
 *author:       alex
 *createDate:   2020/10/28 22:34
 *version:      1.0.0
 */
@Service("RabbitService")
@Slf4j
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(Integer num) {
        int age = 10;
        for (int i = 0; i < num; i++) {
            if ((i & 1) == 1) {
                User user = new User();
                user.setAge(age++);
                user.setName("alex" + i);
                user.setVersion(i);
                rabbitTemplate.convertAndSend("alex.news", "/alex", user);
                log.info("发送信息{}成功", user);
            } else {
                Customer customer = new Customer("tom" + i, null, i);
                rabbitTemplate.convertAndSend("alex.news", "/alex", customer);
                log.info("发送信息{}成功", customer);
            }
        }
    }
}
