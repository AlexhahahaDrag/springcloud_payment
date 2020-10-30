package com.alex.springcloud;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class SpringTest {

    /**
     * description :
     * 测试创建
     * author :     alex
     * @param :
     * @return :
     */
    @Autowired
    private AmqpAdmin rabbitAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void createExchange() {
        String exchangeName = "rabbit-java-exchange";
        Exchange exchange = new DirectExchange(exchangeName);
        rabbitAdmin.declareExchange(exchange);
        log.info("创建交换机[{}]成功", exchangeName);
    }

    @Test
    public void sendMessage() {
        //String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor, @Nullable CorrelationData correlationData)
        rabbitTemplate.convertAndSend("alex.news", "/alex.news", "hello rabbit");
        log.info("发送信息成功{}", "hello rabbit");
    }
}
