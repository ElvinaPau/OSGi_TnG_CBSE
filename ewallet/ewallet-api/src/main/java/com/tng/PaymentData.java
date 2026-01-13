package com.tng;

import java.time.LocalDateTime;

public class PaymentData {
    private String userId;
    private double amount;
    private String description; // e.g., "Tesco", "Wallet Top-Up"
    private String type;        // "RETAIL" or "TOPUP"
    private LocalDateTime timestamp;

    public PaymentData(String userId, double amount, String description, String type) {
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s - RM %.2f", timestamp, type, description, amount);
    }
    
    public String getUserId() { return userId; }
}