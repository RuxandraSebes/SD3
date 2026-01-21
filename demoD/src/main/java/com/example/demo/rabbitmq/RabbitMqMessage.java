package com.example.demo.rabbitmq;

import com.example.demo.services.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqMessage {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqMessage.class);

    private final DeviceService deviceService;

    @Autowired
    public RabbitMqMessage(DeviceService deviceService) { // <-- CONSTRUCTOR MODIFICAT
        this.deviceService = deviceService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.device}")
    public void receivePersonDeletionMessage(PersonDeletionMessage message) {

        LOGGER.info("Received delete for personId {}", message.getPersonId());
        deviceService.removeDevicesForUser(message.getPersonId());
    }
}