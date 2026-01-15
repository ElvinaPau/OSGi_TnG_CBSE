package com.tng.commands;

import com.tng.Wallet;
import com.tng.WalletService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "balance", description = "Check wallet balance by phone number")
@Service
public class BalanceCommand implements Action {

    @Reference
    private WalletService walletService;

    @Argument(index = 0, name = "phoneNumber", description = "User's phone number", required = true)
    private String phoneNumber;

    @Override
    public Object execute() throws Exception {
        if (walletService == null) {
            System.err.println("ERROR: WalletService not available!");
            return null;
        }

        Wallet wallet = walletService.getWallet(phoneNumber);
        if (wallet == null) {
            System.out.println("No wallet found for phone number '" + phoneNumber + "'");
            System.out.println("Create wallet using: ewallet:create-wallet " + phoneNumber + " <username> <initial-balance>");
            return null;
        }

        System.out.println("=== Wallet Balance ===");
        System.out.println("  Phone Number: " + phoneNumber);
        System.out.printf("  Balance: RM %.2f%n", wallet.getBalance());

        return null;
    }
}
