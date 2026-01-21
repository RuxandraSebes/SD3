package com.example.demo.rabbitmq;

import com.example.demo.dtos.PersonAuthRequestDTO;
import com.example.demo.dtos.builders.PersonBuilder;
import com.example.demo.entities.Person;
import com.example.demo.repositories.PersonRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import com.example.demo.dtos.PersonDetailsDTO;

@Service
public class PersonQueueListener {

    private final PersonRepository personRepository;
    private final RabbitMqMessageSender rabbitMqMessageSender;

    @Autowired
    public PersonQueueListener(PersonRepository personRepository, RabbitMqMessageSender rabbitMqMessageSender) {
        this.personRepository = personRepository;
        this.rabbitMqMessageSender = rabbitMqMessageSender;
    }

    @RabbitListener(queues = "${rabbitmq.queue.people}")
    public void handlePersonCreated(PersonAuthRequestDTO dto) {
        Person person;
        Optional<Person> existing = personRepository.findByAuthUserId(dto.getAuthUserId());
        if (existing.isPresent()) {
            person = existing.get();
            person.setName(dto.getName());
            person.setAge(dto.getAge());
            person.setAddress(dto.getAddress());
            person = personRepository.save(person);
        } else {
            person = PersonBuilder.toEntity(dto);
            person = personRepository.save(person);
        }

        // Fix: Broadcast the created/updated person to the Sync Exchange so demoD
        // receives it
        PersonDetailsDTO personDetailsDTO = PersonBuilder.toPersonDetailsDTO(person);
        rabbitMqMessageSender.sendPersonCreated(personDetailsDTO);
    }
}
