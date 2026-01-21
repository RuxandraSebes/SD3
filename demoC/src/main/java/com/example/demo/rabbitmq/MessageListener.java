package com.example.demo.rabbitmq;

import com.example.demo.dtos.ChatMessage;
import com.example.demo.dtos.OverconsumptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "queueNotifications")
    public void receiveOverconsumptionAlert(OverconsumptionMessage alert) {
        LOGGER.info("Received overconsumption alert for user {}: device={}, value={}",
                alert.getUserId(), alert.getDeviceId(), alert.getConsumption());

        // Create a chat message to send via WebSocket
        ChatMessage notification = new ChatMessage(
                "System",
                "ALERT: Device " + alert.getDeviceId() + " is overconsuming! Current: " + alert.getConsumption()
                        + " kWh, Limit: " + alert.getMaxLimit() + " kWh",
                ChatMessage.MessageType.ALERT);

        // Send to a specific topic for notifications
        // We can send to /topic/notifications or a more specific user one
        // /user/{userId}/queue/notifications
        // For simplicity, let's go with /topic/notifications for now, or /topic/alerts

        messagingTemplate.convertAndSend("/topic/alerts", notification);

        // Also send to the specific user's private topic
        if (alert.getUserId() != null) {
            messagingTemplate.convertAndSend("/topic/user." + alert.getUserId(), notification);
        }
    }
}
