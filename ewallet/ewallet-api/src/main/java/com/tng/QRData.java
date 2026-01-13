package com.tng;

import java.time.LocalDateTime;

public class QRData {
    private String userId;
    private String qrString;    // The raw string e.g. "Starbucks:15.50"
    private String merchant;
    private double amount;
    private String status;
    private LocalDateTime timestamp;

    public QRData(String userId, String qrString, String merchant, double amount, String status) {
        this.userId = userId;
        this.qrString = qrString;
        this.merchant = merchant;
        this.amount = amount;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("[%s] [QR] %s (RM %.2f) - Raw: %s - Status: %s", timestamp, merchant, amount, qrString, status);
    }

    public String getUserId() { return userId; }
    public String getStatus() { return status; }
}