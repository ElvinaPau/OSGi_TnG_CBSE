package com.tng.commands;

import com.tng.Wallet;
import com.tng.WalletService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "deduct", description = "Deduct balance from wallet by phone number")
@Service
public class DeductCommand implements Action {

    @Reference
    private WalletService walletService;

    @Argument(index = 0, name = "phoneNumber", description = "User's phone number", required = true)
    private String phoneNumber;

    @Argument(index = 1, name = "amount", description = "Amount to deduct", required = true)
    private double amount;

    @Argument(index = 2, name = "description", description = "Description of deduction", required = false)
    private String description = "Manual deduction";

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
        if (oldBalance < amount) {
            System.err.println("Insufficient balance!");
            System.out.printf("  Current Balance: RM %.2f%n", oldBalance);
            System.out.printf("  Requested Amount: RM %.2f%n", amount);
            System.out.printf("  Shortage: RM %.2f%n", amount - oldBalance);
            return null;
        }

        boolean success = walletService.deductBalance(phoneNumber, amount, description);

        if (success) {
            wallet = walletService.getWallet(phoneNumber);
            System.out.println("Balance deducted successfully!");
            System.out.printf("  Amount Deducted: RM %.2f%n", amount);
            System.out.printf("  Description: %s%n", description);
            System.out.printf("  Old Balance: RM %.2f%n", oldBalance);
            System.out.printf("  New Balance: RM %.2f%n", wallet.getBalance());
        } else
            System.err.println("Deduction failed!");
        return null;
    }
}
