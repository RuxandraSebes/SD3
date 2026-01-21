package com.example.demo.rabbitmq;

import com.example.demo.entities.Person;
import com.example.demo.repositories.PersonRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PersonQueueListener {

    private final PersonRepository personRepository;

    public PersonQueueListener(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @RabbitListener(queues = "${rabbitmq.queue.user.sync:queuePeopleSync}")
    public void listen(PersonCreatedMessage message) {
        if (message != null && message.getPerson() != null) {
            // String idStr = String.valueOf(message.getPerson().getId()); // Removed unused
            // variable
            // PersonDetailsDTO in demoD has 'id' (UUID).
            // Person entity in demoD has 'id' (UUID).

            Person person = new Person(
                    message.getPerson().getId(),
                    message.getPerson().getName());
            personRepository.save(person);
            System.out.println("Synced Person: " + person.getName());
        }
    }
}
