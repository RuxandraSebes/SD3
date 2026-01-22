package com.example.demo.rabbitmq;

import com.example.demo.dtos.OverconsumptionMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqMessageSender {

    private final RabbitTemplate rabbitTemplate;

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RabbitMqMessageSender.class);

    @Autowired
    public RabbitMqMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOverconsumptionAlert(OverconsumptionMessage message) {
        LOGGER.info("Sending overconsumption alert for user {} and device {}", message.getUserId(),
                message.getDeviceId());

        rabbitTemplate.convertAndSend("queueNotifications", message);
    }
}
