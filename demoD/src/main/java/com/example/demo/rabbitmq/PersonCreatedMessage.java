package com.example.demo.rabbitmq;

import com.example.demo.dtos.PersonDetailsDTO;

public class PersonCreatedMessage {
    private PersonDetailsDTO person;

    public PersonCreatedMessage() {
    }

    public PersonCreatedMessage(PersonDetailsDTO person) {
        this.person = person;
    }

    public PersonDetailsDTO getPerson() {
        return person;
    }

    public void setPerson(PersonDetailsDTO person) {
        this.person = person;
    }
}
