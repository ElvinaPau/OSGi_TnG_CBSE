package com.tng.commands;

import com.tng.Wallet;
import com.tng.WalletService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "create-wallet", description = "Create a wallet for a user by phone number")
@Service
public class CreateWalletCommand implements Action {

    @Reference
    private WalletService walletService;

    @Argument(index = 0, name = "phoneNumber", description = "User's phone number", required = true)
    private String phoneNumber;

    @Argument(index = 1, name = "username", description = "Username", required = true)
    private String username;

    @Argument(index = 2, name = "initialBalance", description = "Initial balance", required = true)
    private double initialBalance;

    @Override
    public Object execute() throws Exception {
        if (walletService == null) {
            System.err.println("ERROR: WalletService not available!");
            return null;
        }

        Wallet existingWallet = walletService.getWallet(phoneNumber);
        if (existingWallet != null) {
            System.out.println("Wallet already exists for phone number '" + phoneNumber + "'");
            System.out.printf("  Current Balance: RM %.2f%n", existingWallet.getBalance());
            return null;
        }

        Wallet wallet = walletService.findOrCreateWallet(phoneNumber, username, initialBalance);

        System.out.println("Wallet created successfully!");
        System.out.println("  Phone Number: " + phoneNumber);
        System.out.println("  Username: " + username);
        System.out.printf("  Initial Balance: RM %.2f%n", wallet.getBalance());

        return null;
    }
}
