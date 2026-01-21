package com.example.demo.rabbitmq;

import java.util.UUID;

public class PersonDeletionMessage {
    private UUID personId;

    public PersonDeletionMessage() {}
    public PersonDeletionMessage(UUID personId) {
        this.personId = personId;
    }

    public UUID getPersonId() { return personId; }
    public void setPersonId(UUID personId) { this.personId = personId; }
}
