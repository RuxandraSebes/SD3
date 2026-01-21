package com.example.demo.dtos;

public class PeopleRegistrationRequest {
    private Long authUserId;
    private String username;
    private String role;

    private String name;
    private int age;
    private String address;

    public PeopleRegistrationRequest() {
    }

    public PeopleRegistrationRequest(Long authUserId, String username, String role, String name, int age,
            String address) {
        this.authUserId = authUserId;
        this.username = username;
        this.role = role;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public Long getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(Long authUserId) {
        this.authUserId = authUserId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}