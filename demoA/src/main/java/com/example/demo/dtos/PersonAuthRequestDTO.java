package com.example.demo.dtos;
public class PersonAuthRequestDTO {
    private Long authUserId;

    private String name;
    private int age;
    private String address;

    public PersonAuthRequestDTO() {
    }

    public PersonAuthRequestDTO(Long authUserId, String name, int age, String address) {
        this.authUserId = authUserId;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public Long getAuthUserId() { return authUserId; }
    public void setAuthUserId(Long authUserId) { this.authUserId = authUserId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}