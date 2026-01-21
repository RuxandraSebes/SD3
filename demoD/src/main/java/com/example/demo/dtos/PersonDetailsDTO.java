package com.example.demo.dtos;

import java.util.UUID;

public class PersonDetailsDTO {

    private UUID id;
    private String name;
    private String address;
    private Integer age;

    private Long authUserId;

    public Long getAuthUserId() { return authUserId; }
    public void setAuthUserId(Long authUserId) { this.authUserId = authUserId; }

    public PersonDetailsDTO() {
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}