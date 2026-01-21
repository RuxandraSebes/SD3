package com.example.demo.repositories;

import com.example.demo.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {
}
