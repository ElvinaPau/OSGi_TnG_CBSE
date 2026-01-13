package com.tng;

import java.time.LocalDateTime;

public class AutoPayData {
    private String userId;
    private String biller;
    private double amount;
    private String status;
    private LocalDateTime lastExecuted;

    public AutoPayData(String userId, String biller, double amount, String status) {
        this.userId = userId;
        this.biller = biller;
        this.status = status;
        this.amount = amount;
        this.lastExecuted = null; // Not executed yet
    }

    public void updateLastExecuted() {
        this.lastExecuted = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("[AutoPay] %s - RM %.2f [%s] (Last Run: %s)", biller, amount, status, lastExecuted);
    }

    public String getUserId() { return userId; }
    public String getBiller() { return biller; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
}
