package com.tng;

import java.util.List;

public interface WalletService {
    Wallet findOrCreateWallet(String phoneNumber, String username, double initialBalance);
    Wallet getWallet(String phoneNumber);
    Wallet addMoney(String phoneNumber, double amount);
    boolean sendMoney(String senderPhoneNumber, String recipientPhoneNumber, double amount);
    List<Wallet.Transaction> getTransactionHistory(String phoneNumber);
}
