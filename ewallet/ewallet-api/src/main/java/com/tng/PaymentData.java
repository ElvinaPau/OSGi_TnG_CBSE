package com.tng;

import java.time.LocalDateTime;

public class PaymentData {
    private String userId;
    private double amount;
    private String description; // e.g., "Tesco", "Wallet Top-Up"
    private String type;        // "RETAIL" or "TOPUP"
    private String status;
    private LocalDateTime timestamp;

    public PaymentData(String userId, double amount, String description, String type, String status) {
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s - RM %.2f - Status: %s", timestamp, type, description, amount, status);
    }
    
    public String getUserId() { return userId; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
}