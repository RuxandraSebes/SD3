package com.example.demo.security;

public class UserAuthInfo {
    private final Long userId;
    private final String role;

    public UserAuthInfo(Long userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    public boolean isUser() {
        return "user".equalsIgnoreCase(role);
    }
}