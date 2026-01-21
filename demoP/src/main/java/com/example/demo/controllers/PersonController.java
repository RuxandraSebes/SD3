package com.example.demo.controllers;

import com.example.demo.dtos.PersonAuthRequestDTO;
import com.example.demo.dtos.PersonDetailsDTO;
import com.example.demo.security.UserAuthInfo;
import com.example.demo.services.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/people")
@Validated
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<PersonDetailsDTO>> getPeople(
            @AuthenticationPrincipal UserAuthInfo userAuthInfo
    ) {
        return ResponseEntity.ok(personService.findPersons(userAuthInfo));
    }

    @GetMapping("/by-auth/{authUserId}")
    public ResponseEntity<PersonDetailsDTO> getPersonByAuthId(@PathVariable Long authUserId) {
        PersonDetailsDTO person = personService.findPersonByAuthId(authUserId);
        return ResponseEntity.ok(person);
    }

    @PostMapping
    public ResponseEntity<PersonDetailsDTO> create(@Valid @RequestBody PersonDetailsDTO person) {
        UUID id = personService.insert(person);
        PersonDetailsDTO saved = personService.findPersonById(id);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/internal-auth-insert")
    public ResponseEntity<String> createFromAuth(@RequestBody PersonAuthRequestDTO authRequest) {
        try {
            UUID id = personService.insertFromAuth(authRequest);
            return ResponseEntity.ok("Person entry created successfully with UUID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating person from Auth data: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDetailsDTO> getPerson(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserAuthInfo userAuthInfo
    ) {
        PersonDetailsDTO person = personService.findPersonById(id, userAuthInfo);
        return ResponseEntity.ok(person);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDetailsDTO>updatePerson(
            @PathVariable UUID id,
            @Valid @RequestBody PersonDetailsDTO person,
            @AuthenticationPrincipal UserAuthInfo userAuthInfo
    ){
        PersonDetailsDTO updatedPerson = personService.update(id, person, userAuthInfo);
        return ResponseEntity.ok(updatedPerson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable UUID id){
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }
}