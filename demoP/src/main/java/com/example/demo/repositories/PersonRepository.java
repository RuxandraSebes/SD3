package com.example.demo.repositories;

import com.example.demo.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {

    @Query(value = "SELECT p " +
            "FROM Person p " +
            "WHERE p.id = :id ")
    Optional<Person> findById(@Param("id") UUID id);

    Optional<Person> findByAuthUserId(Long authUserId);
}
