package com.example.demo.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RabbitMqMessageSender {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.devices.deletion}")
    private String DEVICES_DELETION_QUEUE;

    @Value("${rabbitmq.queue.monitoring}")
    private String MONITORING_QUEUE;

    public RabbitMqMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDevicesDeleted(UUID deviceId) {
        DevicesDeletedMessage message = new DevicesDeletedMessage(deviceId);

        rabbitTemplate.convertAndSend(DEVICES_DELETION_QUEUE, message);
    }

    public void sendDeviceToMonitoring(UUID deviceId, Long userId, int maxConsumption) {
        DeviceCreatedMessage message = new DeviceCreatedMessage(deviceId, userId, (double) maxConsumption);
        rabbitTemplate.convertAndSend("sync.exchange", "device.created", message);
    }
}
