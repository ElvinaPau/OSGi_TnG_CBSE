package com.tng.wallet.impl;

import com.tng.User;
import com.tng.UserService;
import com.tng.Wallet;
import com.tng.WalletService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = WalletService.class)
public class WalletServiceImpl implements WalletService {

    // In-memory store of wallets keyed by userId
    private final Map<String, Wallet> wallets = new ConcurrentHashMap<>();

    @Reference
    private UserService userService;

    @Override
    public Wallet findOrCreateWallet(String phoneNumber, String username, double initialBalance) {
        // Find or create user
        User user = userService.findOrCreateUser(phoneNumber, username);

        // Check if wallet exists
        Wallet wallet = wallets.get(user.getId());
        if (wallet == null) {
            wallet = new Wallet(user.getId(), initialBalance);
            wallets.put(user.getId(), wallet);

            // Record initial deposit if balance > 0
            if (initialBalance > 0) {
                wallet.addTransaction("TOP_UP", initialBalance, "Initial balance");
            }
        }
        return wallet;
    }

    @Override
    public Wallet getWallet(String phoneNumber) {
        User user = userService.getUser(phoneNumber);
        if (user == null)
            return null;
        return wallets.get(user.getId());
    }

    @Override
    public Wallet addMoney(String phoneNumber, double amount) {
        Wallet wallet = getWallet(phoneNumber);
        if (wallet != null && amount > 0) {
            wallet.setBalance(wallet.getBalance() + amount);
            wallet.addTransaction("TOP_UP", amount, "Added money");
        }
        return wallet;
    }

    @Override
    public boolean deductBalance(String phoneNumber, double amount, String description) {
        Wallet wallet = getWallet(phoneNumber);
        if (wallet != null && wallet.getBalance() >= amount) {
            wallet.setBalance(wallet.getBalance() - amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean sendMoney(String senderPhoneNumber, String recipientPhoneNumber, double amount) {
        if (senderPhoneNumber.equals(recipientPhoneNumber))
            return false;

        Wallet senderWallet = getWallet(senderPhoneNumber);
        Wallet recipientWallet = getWallet(recipientPhoneNumber);

        if (senderWallet == null || recipientWallet == null || senderWallet.getBalance() < amount) {
            return false;
        }

        User senderUser = userService.getUser(senderPhoneNumber);
        User recipientUser = userService.getUser(recipientPhoneNumber);

        // Deduct from sender, add to recipient
        senderWallet.setBalance(senderWallet.getBalance() - amount);
        recipientWallet.setBalance(recipientWallet.getBalance() + amount);

        // Record transactions
        senderWallet.addTransaction("SEND", amount,
                "Sent to " + recipientUser.getUsername() + " (" + recipientPhoneNumber + ")");
        recipientWallet.addTransaction("RECEIVE", amount,
                "Received from " + senderUser.getUsername() + " (" + senderPhoneNumber + ")");

        return true;
    }

    @Override
    public List<Wallet.Transaction> getTransactionHistory(String phoneNumber) {
        Wallet wallet = getWallet(phoneNumber);
        return wallet != null ? wallet.getTransactions() : List.of();
    }
}
