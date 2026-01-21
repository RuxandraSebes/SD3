package com.example.demo.rabbitmq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.dtos.PersonAuthRequestDTO;
import com.example.demo.service.AuthService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

@Service
public class RabbitMqMessage {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqMessage.class);

    private final RabbitTemplate rabbitTemplate;
    private final AuthService authService;

    @Value("${rabbitmq.queue.people}")
    private String PEOPLE_QUEUE;

    @Value("${rabbitmq.queue.auth.deletion}")
    private String AUTH_DELETION_QUEUE;


    @Autowired
    public RabbitMqMessage(RabbitTemplate rabbitTemplate, @Lazy AuthService authService) {
        this.rabbitTemplate = rabbitTemplate;
        this.authService = authService;
    }

    public void sendToPeopleQueue(PersonAuthRequestDTO message) {
        rabbitTemplate.convertAndSend(PEOPLE_QUEUE, message);
    }

    @RabbitListener(queues = "${rabbitmq.queue.auth.deletion}")
    public void receiveAuthDeletionMessage(AuthDeletionMessage message) {
        try {
            LOGGER.info("Received Auth Deletion Message for Auth ID: {}", message.getAuthUserId());
            authService.deleteUserByAuthUserId(message.getAuthUserId());
            LOGGER.info("Successfully deleted User from Auth for Auth ID: {}", message.getAuthUserId());
        } catch (Exception e) {
            LOGGER.error("Error processing Auth deletion message for ID {}: {}", message.getAuthUserId(), e.getMessage(), e);
        }
    }

}
