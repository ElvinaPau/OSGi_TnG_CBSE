package com.tng.commands;

import com.tng.Wallet;
import com.tng.WalletService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "send-money", description = "Send money from one user to another by phone number")
@Service
public class SendMoneyCommand implements Action {

    @Reference
    private WalletService walletService;

    @Argument(index = 0, name = "senderPhone", description = "Sender's phone number", required = true)
    private String senderPhone;

    @Argument(index = 1, name = "recipientPhone", description = "Recipient's phone number", required = true)
    private String recipientPhone;

    @Argument(index = 2, name = "amount", description = "Amount to send", required = true)
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

        if (senderPhone.equals(recipientPhone)) {
            System.err.println("ERROR: Sender and recipient cannot be the same.");
            return null;
        }

        Wallet senderWallet = walletService.getWallet(senderPhone);
        Wallet recipientWallet = walletService.getWallet(recipientPhone);

        if (senderWallet == null) {
            System.err.println("Sender wallet not found for phone number: " + senderPhone);
            return null;
        }

        if (recipientWallet == null) {
            System.err.println("Recipient wallet not found for phone number: " + recipientPhone);
            return null;
        }

        if (senderWallet.getBalance() < amount) {
            System.err.println("Insufficient balance!");
            System.out.printf("  Sender Balance: RM %.2f%n", senderWallet.getBalance());
            System.out.printf("  Requested Amount: RM %.2f%n", amount);
            return null;
        }

        double oldBalance = senderWallet.getBalance();

        boolean success = walletService.sendMoney(senderPhone, recipientPhone, amount);

        if (success) {
            senderWallet = walletService.getWallet(senderPhone);
            recipientWallet = walletService.getWallet(recipientPhone);

            System.out.println("Money sent successfully!");
            System.out.printf("  Amount: RM %.2f%n", amount);
            System.out.printf("  Old Balance: RM %.2f%n", oldBalance);
            System.out.printf("  New Balance: RM %.2f%n", senderWallet.getBalance());
        } else {
            System.err.println("Transfer failed!");
        }

        return null;
    }
}
