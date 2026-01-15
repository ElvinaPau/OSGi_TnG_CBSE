package com.tng.commands;

import com.tng.PaymentService;
import com.tng.User;
import com.tng.UserService;
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

    @Reference
    private UserService userService;

    @Argument(index = 0, name = "phoneNumber", description = "User Phone Number", required = true)
    private String phoneNumber;

    @Argument(index = 1, name = "merchant", required = true)
    private String merchant;

    @Argument(index = 2, name = "amount", required = true)
    private double amount;

    @Override
    public Object execute() throws Exception {
        if (paymentService == null) {
            System.err.println("Error: PaymentService is not available.");
            return null;
        }

        // Fetch username for display
        User user = userService.getUser(phoneNumber);
        String username = (user != null) ? user.getUsername() : phoneNumber;

        boolean success = paymentService.processPayment(phoneNumber, username, amount, merchant);

        if (success) {
            System.out.println("Payment Successful!");
            System.out.printf("%s paid RM %.2f to %s%n", username, amount, merchant);
        } else {
            System.err.println("Payment Failed! Check balance or user existence.");
        }
        return null;
    }
}