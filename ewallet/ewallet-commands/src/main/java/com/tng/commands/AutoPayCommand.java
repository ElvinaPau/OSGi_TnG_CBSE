package com.tng.commands;

import com.tng.PaymentService;
import com.tng.User;
import com.tng.UserService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "autopay", description = "Manage AutoPay settings (setup/run)")
@Service
public class AutoPayCommand implements Action {

    @Reference
    private PaymentService paymentService;

    @Reference
    private UserService userService; // add this to fetch username

    @Argument(index = 0, name = "phoneNumber", description = "User Phone Number", required = true, multiValued = false)
    private String phoneNumber;

    @Argument(index = 1, name = "action", description = "Action: 'setup' or 'run'", required = true, multiValued = false)
    private String action;

    @Argument(index = 2, name = "biller", description = "Biller Name (Required for setup)", required = false, multiValued = false)
    private String biller;

    @Argument(index = 3, name = "amount", description = "Deduction Amount (Required for setup)", required = false, multiValued = false)
    private Double amount;

    @Override
    public Object execute() throws Exception {
        if (paymentService == null) {
            System.err.println("Error: PaymentService is not available.");
            return null;
        }

        User user = userService.getUser(phoneNumber);
        String username = (user != null) ? user.getUsername() : phoneNumber;

        if ("setup".equalsIgnoreCase(action)) {
            if (biller == null || amount == null) {
                System.err.println("Error: 'setup' requires <biller> and <amount> arguments.");
                System.out.println("Usage: ewallet:autopay <phoneNumber> setup <biller> <amount>");
                return null;
            }
            paymentService.registerAutoPay(phoneNumber, biller, amount);
            System.out.printf("AutoPay registered for %s (%s): %s (RM %.2f)%n",
                    username, phoneNumber, biller, amount);

        } else if ("run".equalsIgnoreCase(action)) {
            System.out.printf("Simulating AutoPay execution for user: %s (%s)%n", username, phoneNumber);
            paymentService.runAutoPaySimulation(phoneNumber);
            System.out.println("Simulation complete. Check history/balance for results.");

        } else {
            System.err.println("Invalid action. Use 'setup' or 'run'.");
        }

        return null;
    }
}
