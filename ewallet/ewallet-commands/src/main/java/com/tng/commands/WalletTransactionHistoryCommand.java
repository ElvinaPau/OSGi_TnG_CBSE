package com.tng.commands;

import com.tng.User;
import com.tng.UserService;
import com.tng.Wallet;
import com.tng.Wallet.Transaction;
import com.tng.WalletService;
import java.util.List;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "wallet-history", description = "View wallet transaction history by phone number")
@Service
public class WalletTransactionHistoryCommand implements Action {

    @Reference
    private WalletService walletService;

    @Reference
    private UserService userService;

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
            System.out.println("Use: ewallet:create-wallet " + phoneNumber + " <username> <initial-balance>");
            return null;
        }

        User user = userService.getUser(phoneNumber);

        List<Transaction> transactions = walletService.getTransactionHistory(phoneNumber);

        System.out.println("=== Transaction History ===");
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Username: " + (user != null ? user.getUsername() : "Unknown"));
        System.out.printf("Current Balance: RM %.2f%n", wallet.getBalance());
        System.out.println("-------------------------------");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (Transaction tx : transactions) {
                System.out.printf("[%s] %s: RM %.2f - %s%n",
                        tx.getTimestamp(), tx.getType(), tx.getAmount(), tx.getDescription());
            }
        }

        return null;
    }
}
