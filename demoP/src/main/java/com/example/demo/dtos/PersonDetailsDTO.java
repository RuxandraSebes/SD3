// demoP/dtos/PersonDetailsDTO.java (MODIFICAT)
package com.example.demo.dtos;


import com.example.demo.dtos.validators.annotation.AgeLimit;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public class PersonDetailsDTO {

    private UUID id;

    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "address is required")
    private String address;
    @NotNull(message = "age is required")
    @AgeLimit(value = 18)
    @Max(value = 100, message = "Maximum age is 100 years old.")
    private Integer age;

    // NOU: Câmp adăugat pentru comunicarea internă
    private Long authUserId;

    public PersonDetailsDTO() {
    }


    public PersonDetailsDTO(UUID id, String name, String address, int age) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.age = age;
    }

    // Constructor NOU/MODIFICAT, dacă este necesar în altă parte
    public PersonDetailsDTO(UUID id, String name, String address, int age, Long authUserId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.age = age;
        this.authUserId = authUserId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // NOU: Getter și Setter pentru authUserId
    public Long getAuthUserId() { return authUserId; }
    public void setAuthUserId(Long authUserId) { this.authUserId = authUserId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDetailsDTO that = (PersonDetailsDTO) o;
        return age == that.age &&
                Objects.equals(name, that.name) &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, age);
    }
}