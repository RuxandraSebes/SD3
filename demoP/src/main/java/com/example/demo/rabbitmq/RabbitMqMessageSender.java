package com.example.demo.rabbitmq;

import com.example.demo.dtos.PersonDetailsDTO;
import com.example.demo.rabbitmq.AuthDeletionMessage;
import com.example.demo.rabbitmq.PersonCreatedMessage;
import com.example.demo.rabbitmq.PersonDeletionMessage;
import com.example.demo.rabbitmq.PersonDeletionMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RabbitMqMessageSender {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.device}")
    private String DEVICE_QUEUE;

    @Value("${rabbitmq.queue.auth.deletion}")
    private String AUTH_DELETION_QUEUE;

    @Value("${rabbitmq.queue.people}")
    private String PEOPLE_QUEUE;

    public RabbitMqMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendPersonCreated(PersonDetailsDTO dto) {
        PersonCreatedMessage message = new PersonCreatedMessage(dto);
        rabbitTemplate.convertAndSend("sync.exchange", "person.created", message);
    }

    public void sendPersonDeletionToDevices(UUID personId) {
        rabbitTemplate.convertAndSend(DEVICE_QUEUE, new PersonDeletionMessage(personId));
    }

    public void sendAuthDeletionMessage(Long authUserId) {
        rabbitTemplate.convertAndSend(AUTH_DELETION_QUEUE, new AuthDeletionMessage(authUserId));
    }

}
