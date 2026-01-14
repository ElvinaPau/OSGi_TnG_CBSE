package com.tng.commands;

import com.tng.PaymentService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "pay", description = "Make a payment to a merchant")
@Service
public class PayCommand implements Action {

    @Reference
    private PaymentService paymentService;

    @Argument(index = 0, name = "username", required = true)
    private String username;

    @Argument(index = 1, name = "merchant", required = true)
    private String merchant;

    @Argument(index = 2, name = "amount", required = true)
    private double amount;

    @Override
    public Object execute() throws Exception {
        boolean success = paymentService.processPayment(username, amount, merchant);
        
        if (success) {
            System.out.println("Payment Successful!");
            System.out.printf("Paid RM %.2f to %s%n", amount, merchant);
        } else {
            System.err.println("Payment Failed! Check balance or user existence.");
        }
        return null;
    }
}