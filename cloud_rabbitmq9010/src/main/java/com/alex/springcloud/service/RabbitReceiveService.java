package com.alex.springcloud.service;

import com.alex.springcloud.bean.Customer;
import com.alex.springcloud.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 *description:  rabbit 信息接受服务
 *author:       alex
 *createDate:   2020/10/28 22:33
 *version:      1.0.0
 */
@Service("RabbitReceiveService")
@RabbitListener(queues = {"alex.news"})
@Slf4j
public class RabbitReceiveService {

    @RabbitHandler
    public void receiverMessage(Message message, User user) {
        log.info("获取消息{}", user);
        log.info(message.toString());
        log.info("==========================================");
    }

    @RabbitHandler
    public void receiverMessage(Customer customer) {
        log.info("获取消息{}", customer);
    }
}
