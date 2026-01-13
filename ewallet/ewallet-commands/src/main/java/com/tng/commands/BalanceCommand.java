package com.tng.commands;

import com.tng.Wallet;
import com.tng.WalletService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "balance", description = "Check wallet balance")
@Service
public class BalanceCommand implements Action {

    @Reference
    private WalletService walletService;

    @Argument(index = 0, name = "username", description = "Username", required = true)
    private String username;

    @Override
    public Object execute() throws Exception {
        if (walletService == null) {
            System.err.println("ERROR: WalletService not available!");
            return null;
        }

        Wallet wallet = walletService.getWallet(username);
        if (wallet == null) {
            System.out.println("No wallet found for user '" + username + "'");
            System.out.println("Use: ewallet:create-wallet " + username + " <initial-balance>");
            return null;
        }

        System.out.println("=== Wallet Balance ===");
        System.out.println("  Username: " + username);
        System.out.printf("  Balance: RM %.2f%n", wallet.getBalance());

        return null;
    }
}