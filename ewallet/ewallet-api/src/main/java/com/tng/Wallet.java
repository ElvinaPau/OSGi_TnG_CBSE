package com.tng;

import java.util.UUID;

public class Wallet {
    private final String id;

    private String userId;
    private double balance;

    public Wallet() {
        this.id = UUID.randomUUID().toString();
    }

    public Wallet(String userId, double balance) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
