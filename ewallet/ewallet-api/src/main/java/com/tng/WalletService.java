package com.tng;

public interface WalletService {
    Wallet findOrCreateWallet(String username, double initialBalance);
    Wallet getWallet(String username);
    Wallet addFunds(String username, double amount);
    boolean deductBalance(String username, double amount, String description);
}
