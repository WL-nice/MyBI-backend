package com.wanglei.mybibackend.mq;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class BiMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     *
     * @param message
     */
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(MQConstant.BI_EXCHANGE_NAME, MQConstant.BI_ROUTING_KEY, message);
    }
}
