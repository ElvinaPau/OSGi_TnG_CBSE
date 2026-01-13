package com.tng.wallet.impl;

import com.tng.User;
import com.tng.UserService;
import com.tng.Wallet;
import com.tng.WalletService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = WalletService.class)
public class WalletServiceImpl implements WalletService {

    // In-memory store simulating wallets
    private final Map<String, Wallet> wallets = new ConcurrentHashMap<>();

    @Reference
    private UserService userService;

    @Override
    public Wallet findOrCreateWallet(String username, double initialBalance) {
        // Find user first
        User user = userService.findOrCreateUser(username);

        // Check if wallet exists
        Wallet wallet = wallets.get(user.getId());
        if (wallet == null) {
            wallet = new Wallet(user.getId(), initialBalance);
            wallets.put(user.getId(), wallet);
        }
        return wallet;
    }

    @Override
    public Wallet getWallet(String username) {
        User user = userService.getUser(username);
        if (user == null)
            return null;
        return wallets.get(user.getId());
    }

    @Override
    public Wallet addFunds(String username, double amount) {
        Wallet wallet = getWallet(username);
        if (wallet != null) {
            wallet.setBalance(wallet.getBalance() + amount);
        }
        return wallet;
    }

    @Override
    public boolean deductBalance(String username, double amount, String description) {
        Wallet wallet = getWallet(username);
        if (wallet != null && wallet.getBalance() >= amount) {
            wallet.setBalance(wallet.getBalance() - amount);
            System.out
                    .println("[WALLET LOG]: Deducted RM " + amount + " from " + username + ". Reason: " + description);
            return true;
        }
        return false;
    }
}