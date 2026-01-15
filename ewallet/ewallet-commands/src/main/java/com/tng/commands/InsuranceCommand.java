package com.tng.commands;

import com.tng.InsuranceService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import java.util.List;

@Command(scope = "ewallet", name = "insurance", description = "Manage Insurance (buy/view/claim)")
@Service
public class InsuranceCommand implements Action {

    @Reference
    private InsuranceService insuranceService;

    @Argument(index = 0, name = "action", description = "Action: buy / view / claim", required = true)
    private String action;

    @Argument(index = 1, name = "username", description = "User Name", required = true)
    private String username;

    @Argument(index = 2, name = "detail", description = "Plate No (for buy) or Policy ID (for claim)", required = false)
    private String detail;

    @Override
    public Object execute() throws Exception {
        if ("buy".equals(action)) {
            if (detail == null) {
                System.out.println("Error: Please provide Plate Number.");
                return null;
            }
            insuranceService.purchaseMotorPolicy(username, detail);

        } else if ("view".equals(action)) {
            List<String> policies = insuranceService.viewPolicies(username);
            if (policies.isEmpty()) {
                System.out.println("No policies found for " + username);
            } else {
                System.out.println("--- Policies for " + username + " ---");
                for (String p : policies) {
                    System.out.println(p);
                }
            }

        } else if ("claim".equals(action)) {
            if (detail == null) {
                System.out.println("Error: Please provide Policy ID.");
                return null;
            }
            insuranceService.submitClaim(username, detail);

        } else {
            System.out.println("Unknown action. Try: buy, view, claim");
        }
        return null;
    }
}