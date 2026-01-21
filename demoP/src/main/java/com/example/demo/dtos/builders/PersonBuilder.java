package com.example.demo.dtos.builders;

import com.example.demo.dtos.PersonAuthRequestDTO;
import com.example.demo.dtos.PersonDTO;
import com.example.demo.dtos.PersonDetailsDTO;
import com.example.demo.entities.Person;

public class PersonBuilder {

    private PersonBuilder() {
    }

    public static PersonDTO toPersonDTO(Person person) {
        return new PersonDTO(person.getId(), person.getName(), person.getAge());
    }

    // METODA MODIFICATĂ
    public static PersonDetailsDTO toPersonDetailsDTO(Person person) {
        PersonDetailsDTO dto = new PersonDetailsDTO(
                person.getId(),
                person.getName(),
                person.getAddress(),
                person.getAge()
        );
        // NOU: Mapează AuthUserId
        dto.setAuthUserId(person.getAuthUserId());
        return dto;
    }

    public static Person toEntity(PersonDetailsDTO personDetailsDTO) {
        return new Person(personDetailsDTO.getName(),
                personDetailsDTO.getAddress(),
                personDetailsDTO.getAge());
    }

    public static Person toEntity(PersonAuthRequestDTO authRequestDTO) {
        Person person = new Person(
                authRequestDTO.getName(),
                authRequestDTO.getAddress(),
                authRequestDTO.getAge()
        );

        person.setAuthUserId(authRequestDTO.getAuthUserId());

        return person;
    }
}
