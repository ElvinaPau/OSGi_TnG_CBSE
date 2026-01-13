package com.tng.commands;

import com.tng.PaymentService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "topup", description = "Top-up wallet funds (Logged via Payment Service)")
@Service
public class TopUpCommand implements Action {

    @Reference
    private PaymentService paymentService;

    @Argument(index = 0, name = "username", description = "User ID", required = true, multiValued = false)
    private String username;

    @Argument(index = 1, name = "amount", description = "Amount to top up", required = true, multiValued = false)
    private double amount;

    @Override
    public Object execute() throws Exception {
        if (paymentService == null) {
            System.err.println("Error: PaymentService is not available.");
            return null;
        }

        if (amount <= 0) {
            System.err.println("Error: Top-up amount must be positive.");
            return null;
        }

        System.out.println("Processing top-up for " + username + "...");
        boolean success = paymentService.processTopUp(username, amount);

        if (success) {
            System.out.println("Top-Up Successful!");
            System.out.printf("Added RM %.2f to %s's wallet.%n", amount, username);
        } else {
            System.err.println("Top-Up Failed. Please check user existence.");
        }
        return null;
    }
}
