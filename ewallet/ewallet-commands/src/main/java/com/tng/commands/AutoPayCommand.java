package com.tng.commands;

import com.tng.PaymentService;
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

    @Argument(index = 0, name = "username", description = "User ID", required = true, multiValued = false)
    private String username;

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

        if ("setup".equalsIgnoreCase(action)) {
            // Validate inputs for setup
            if (biller == null || amount == null) {
                System.err.println("Error: 'setup' requires <biller> and <amount> arguments.");
                System.out.println("Usage: ewallet:autopay <user> setup <biller> <amount>");
                return null;
            }
            paymentService.registerAutoPay(username, biller, amount);
            System.out.printf("AutoPay registered for %s: %s (RM %.2f)%n", username, biller, amount);

        } else if ("run".equalsIgnoreCase(action)) {
            // Run simulation
            System.out.println("Simulating AutoPay execution for user: " + username);
            paymentService.runAutoPaySimulation(username);
            System.out.println("Simulation complete. Check history/balance for results.");

        } else {
            System.err.println("Invalid action. Use 'setup' or 'run'.");
        }

        return null;
    }
}