package com.example.demo.rabbitmq;

import com.example.demo.dtos.DeviceCreatedMessage;
import com.example.demo.rabbitmq.DevicesDeletedMessage;
import com.example.demo.dtos.MeasurementMessage;
import com.example.demo.service.MonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    private final MonitoringService monitoringService;

    @Autowired
    public MessageListener(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.monitoring}")
    public void handleDeviceCreated(DeviceCreatedMessage message) {
        LOGGER.info("Received DeviceCreatedMessage for device {}", message.getId());
        monitoringService.registerDevice(message);
    }

    @RabbitListener(queues = "${rabbitmq.queue.devices.deletion}")
    public void handleDevicesDeletion(DevicesDeletedMessage message) {
        if (message.getDeviceId() == null) {
            LOGGER.warn("Received DevicesDeletedMessage with null deviceId. Skipping.");
            return;
        }
        LOGGER.info("Received DevicesDeletedMessage for devices {}", message.getDeviceId());
        monitoringService.deleteDeviceById(message.getDeviceId());
    }

    @RabbitListener(queues = "${rabbitmq.queue.measurements}")
    public void handleMeasurement(MeasurementMessage message) {
        LOGGER.info("Received MeasurementMessage for device {}", message.getDeviceId());
        monitoringService.saveMeasurement(message);
    }
}
