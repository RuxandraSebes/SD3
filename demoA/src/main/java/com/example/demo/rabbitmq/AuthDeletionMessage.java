package com.example.demo.rabbitmq;

public class AuthDeletionMessage {
    private Long authUserId;

    public AuthDeletionMessage() {}

    public AuthDeletionMessage(Long authUserId) {
        this.authUserId = authUserId;
    }

    public Long getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(Long authUserId) {
        this.authUserId = authUserId;
    }
}