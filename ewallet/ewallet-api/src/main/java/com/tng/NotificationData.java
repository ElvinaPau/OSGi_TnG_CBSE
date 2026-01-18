package com.tng;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationData {
    private final String id;
    private String userId;
    private String username;
    private String phoneNumber;
    private String type;  // PAYMENT, WALLET, INVESTMENT, AUTOPAY, QR, CLAIM
    private String message;
    private LocalDateTime timestamp;
    private boolean read;

    public NotificationData() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    public NotificationData(String userId, String username, String phoneNumber, String type, String message) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public String toString() {
        String readStatus = read ? "[ ]" : "[*]";
        return String.format("%s [%s] %s - %s - %s", readStatus, type, timestamp, message, username);
    }
}
