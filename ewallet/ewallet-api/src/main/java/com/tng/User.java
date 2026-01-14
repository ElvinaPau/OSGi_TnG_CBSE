package com.tng;

import java.util.UUID;

public class User {
    private final String id;
    private String username;
    private String phoneNumber;

    public User() {
        this.id = UUID.randomUUID().toString();
    }

    public User(String username, String phoneNumber) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
