package com.tng.commands;

import com.tng.Wallet;
import com.tng.WalletService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "add-money", description = "Add money to wallet by phone number")
@Service
public class AddMoneyCommand implements Action {

    @Reference
    private WalletService walletService;

    @Argument(index = 0, name = "phoneNumber", description = "User's phone number", required = true)
    private String phoneNumber;

    @Argument(index = 1, name = "amount", description = "Amount to add", required = true)
    private double amount;

    @Override
    public Object execute() throws Exception {
        if (walletService == null) {
            System.err.println("ERROR: WalletService not available!");
            return null;
        }

        if (amount <= 0) {
            System.err.println("ERROR: Amount must be greater than 0");
            return null;
        }

        Wallet wallet = walletService.getWallet(phoneNumber);
        if (wallet == null) {
            System.out.println("No wallet found for phone number '" + phoneNumber + "'");
            System.out.println("Use: ewallet:create-wallet " + phoneNumber + " <username> <initial-balance>");
            return null;
        }

        double oldBalance = wallet.getBalance();
        walletService.addMoney(phoneNumber, amount);
        wallet = walletService.getWallet(phoneNumber);

        System.out.println("Money added successfully!");
        System.out.printf("  Amount Added: RM %.2f%n", amount);
        System.out.printf("  Old Balance: RM %.2f%n", oldBalance);
        System.out.printf("  New Balance: RM %.2f%n", wallet.getBalance());

        return null;
    }
}
