package com.example.demo.services;

import com.example.demo.dtos.PersonAuthRequestDTO;
import com.example.demo.dtos.PersonDetailsDTO;
import com.example.demo.dtos.builders.PersonBuilder;
import com.example.demo.entities.Person;
import com.example.demo.handlers.exceptions.model.ResourceNotFoundException;
import com.example.demo.rabbitmq.RabbitMqMessageSender;
import com.example.demo.repositories.PersonRepository;
import com.example.demo.security.UserAuthInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;
    private final RestTemplate restTemplate;

    private final RabbitMqMessageSender rabbitMqMessageSender;

    @Value("${service.auth.base-url}")
    private String AUTH_SERVICE_BASE_URL;

    @Value("${service.devices.base-url}")
    private String DEVICES_SERVICE_BASE_URL;

    @Autowired
    public PersonService(PersonRepository personRepository, RestTemplate restTemplate,
            RabbitMqMessageSender rabbitMqMessageSender) {
        this.personRepository = personRepository;
        this.restTemplate = restTemplate;
        this.rabbitMqMessageSender = rabbitMqMessageSender;
    }

    // public List<PersonDetailsDTO> findPersons(UserAuthInfo userAuthInfo) {
    // if (userAuthInfo.isAdmin()) {
    // return personRepository.findAll().stream()
    // .map(PersonBuilder::toPersonDetailsDTO)
    // .collect(Collectors.toList());
    // } else {
    // Person person = personRepository.findByAuthUserId(userAuthInfo.getUserId())
    // .orElseThrow(() -> new ResourceNotFoundException("Person linked to Auth ID: "
    // + userAuthInfo.getUserId()));
    // return List.of(PersonBuilder.toPersonDetailsDTO(person));
    // }
    // }
    public List<PersonDetailsDTO> findPersons(UserAuthInfo userAuthInfo) {

        if (userAuthInfo == null) {
            throw new RuntimeException("Access Denied: Authentication information is missing or invalid.");
        }

        if (userAuthInfo.isAdmin()) {
            return personRepository.findAll().stream()
                    .map(PersonBuilder::toPersonDetailsDTO)
                    .collect(Collectors.toList());
        } else {
            Person person = personRepository.findByAuthUserId(userAuthInfo.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Person linked to Auth ID: " + userAuthInfo.getUserId()));
            return List.of(PersonBuilder.toPersonDetailsDTO(person));
        }
    }

    public PersonDetailsDTO findPersonById(UUID id) {
        Optional<Person> prosumerOptional = personRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }
        return PersonBuilder.toPersonDetailsDTO(prosumerOptional.get());
    }

    public PersonDetailsDTO findPersonByAuthId(Long authUserId) {
        Person person = personRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Person linked to Auth ID: " + authUserId));
        return PersonBuilder.toPersonDetailsDTO(person);
    }

    // public PersonDetailsDTO findPersonById(UUID id, UserAuthInfo userAuthInfo) {
    // PersonDetailsDTO person = findPersonById(id);
    //
    // if (userAuthInfo.isUser()) {
    // Person userPerson =
    // personRepository.findByAuthUserId(userAuthInfo.getUserId())
    // .orElseThrow(() -> new ResourceNotFoundException("Person linked to Auth ID: "
    // + userAuthInfo.getUserId()));
    //
    // if (!person.getId().equals(userPerson.getId())) {
    // LOGGER.error("User with Auth ID {} attempted to access person with id {}",
    // userAuthInfo.getUserId(), id);
    // throw new ResourceNotFoundException("Person with id: " + id);
    // }
    // }
    //
    // return person;
    // }

    public PersonDetailsDTO findPersonById(UUID id, UserAuthInfo userAuthInfo) {
        PersonDetailsDTO person = findPersonById(id);

        if (userAuthInfo != null && userAuthInfo.isUser()) {
            Person userPerson = personRepository.findByAuthUserId(userAuthInfo.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Person linked to Auth ID: " + userAuthInfo.getUserId()));

            if (!person.getId().equals(userPerson.getId())) {
                LOGGER.error("User with Auth ID {} attempted to access person with id {}", userAuthInfo.getUserId(),
                        id);
                throw new ResourceNotFoundException("Person with id: " + id);
            }
        }

        return person;
    }

    public UUID insert(PersonDetailsDTO personDTO) {
        Person person = PersonBuilder.toEntity(personDTO);
        person = personRepository.save(person);

        // Fix: Broadcast to Sync Exchange so demoD receives it
        rabbitMqMessageSender.sendPersonCreated(PersonBuilder.toPersonDetailsDTO(person));

        LOGGER.debug("Person with id {} was inserted in db", person.getId());
        return person.getId();
    }

    // public UUID insertFromAuth(PersonAuthRequestDTO authRequestDTO) {
    // Person person = PersonBuilder.toEntity(authRequestDTO);
    // person = personRepository.save(person);
    // LOGGER.debug("Person linked to Auth ID {} was inserted in db",
    // authRequestDTO.getAuthUserId());
    // return person.getId();
    // }
    // public UUID insertFromAuth(PersonAuthRequestDTO authRequestDTO) {
    // Person person = PersonBuilder.toEntity(authRequestDTO);
    // person = personRepository.save(person);
    //
    // rabbitMqMessageSender.sendPersonCreated(authRequestDTO);
    //
    // LOGGER.debug("Person linked to Auth ID {} was inserted in db",
    // authRequestDTO.getAuthUserId());
    // return person.getId();
    // }

    public UUID insertFromAuth(PersonAuthRequestDTO dto) {
        Optional<Person> existing = personRepository.findByAuthUserId(dto.getAuthUserId());

        if (existing.isPresent()) {
            Person p = existing.get();
            p.setName(dto.getName());
            p.setAge(dto.getAge());
            p.setAddress(dto.getAddress());
            personRepository.save(p);
            return p.getId();
        }

        Person person = PersonBuilder.toEntity(dto);
        person = personRepository.save(person);

        PersonDetailsDTO personDetailsDTO = PersonBuilder.toPersonDetailsDTO(person);
        rabbitMqMessageSender.sendPersonCreated(personDetailsDTO);

        return person.getId();
    }

    public PersonDetailsDTO update(UUID id, PersonDetailsDTO personDetails, UserAuthInfo userAuthInfo) {
        return personRepository.findById(id)
                .map(existingPerson -> {
                    if (userAuthInfo.isUser()) {
                        Person userPerson = personRepository.findByAuthUserId(userAuthInfo.getUserId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Person linked to Auth ID: " + userAuthInfo.getUserId()));

                        if (!existingPerson.getId().equals(userPerson.getId())) {
                            LOGGER.error("User with Auth ID {} attempted to update person with id {}",
                                    userAuthInfo.getUserId(), id);
                            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
                        }
                    }

                    existingPerson.setName(personDetails.getName());
                    existingPerson.setAge(personDetails.getAge());
                    existingPerson.setAddress(personDetails.getAddress());

                    Person savedPerson = personRepository.save(existingPerson);

                    return PersonBuilder.toPersonDetailsDTO(savedPerson);
                })
                .orElseThrow(() -> {
                    LOGGER.error("Person with id {} was not found in db", id);
                    return new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
                });
    }

    @Transactional
    public void delete(UUID personId) {
        Optional<Person> personOptional = personRepository.findById(personId);
        if (!personOptional.isPresent()) {
            throw new ResourceNotFoundException("Person with id " + personId);
        }

        Person person = personOptional.get();
        Long authUserId = person.getAuthUserId();

        rabbitMqMessageSender.sendPersonDeletionToDevices(personId);
        rabbitMqMessageSender.sendAuthDeletionMessage(authUserId);

        personRepository.delete(person);
    }
}
