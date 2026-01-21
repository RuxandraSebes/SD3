package com.example.demo.rabbitmq;

import com.example.demo.dtos.PersonAuthRequestDTO;
import com.example.demo.services.PersonService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqMessage {
    private final PersonService personService;

    @Autowired
    public RabbitMqMessage(PersonService personService) {
        this.personService = personService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.people}")
    public void receiveMessage(PersonAuthRequestDTO message) {
       personService.insertFromAuth(message);
    }
}
