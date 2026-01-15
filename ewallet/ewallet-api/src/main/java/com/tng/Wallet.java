package com.tng;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Wallet {
    private final String id;
    private String userId;
    private double balance;

    private final List<Transaction> transactions = new ArrayList<>();

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

    public List<Transaction> getTransactions() {
        return transactions;
    }

    // Add a transaction record
    public void addTransaction(String type, double amount, String description) {
        transactions.add(new Transaction(type, amount, description));
    }

    // Transaction inner class
    public static class Transaction {
        private final String type;
        private final double amount;
        private final String description;
        private final long timestamp;

        public Transaction(String type, double amount, String description) {
            this.type = type;
            this.amount = amount;
            this.description = description;
            this.timestamp = System.currentTimeMillis();
        }

        public String getType() {
            return type;
        }

        public double getAmount() {
            return amount;
        }

        public String getDescription() {
            return description;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
