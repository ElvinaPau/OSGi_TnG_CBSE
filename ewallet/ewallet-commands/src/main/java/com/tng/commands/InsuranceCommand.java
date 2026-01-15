package com.tng.commands;

import com.tng.InsuranceService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import java.util.List;

@Command(scope = "ewallet", name = "insurance", description = "Insurance operations")
@Service
public class InsuranceCommand implements Action {

    @Reference
    private InsuranceService insuranceService;

    @Argument(index = 0, name = "params", multiValued = true, description = "Command arguments")
    private List<String> params;

    @Override
    public Object execute() throws Exception {
        if (params == null || params.isEmpty()) {
            System.out.println("Usage: ewallet:insurance [buy/view/claim] [args...]");
            return null;
        }

        String action = params.get(0);

        switch (action) {
            case "buy":
                // buy <user> <phone> <plate>
                if (params.size() >= 4) {
                    insuranceService.purchaseMotorPolicy(params.get(1), params.get(2), params.get(3));
                } else {
                    System.out.println("Error: Use buy <user> <phone> <plate>");
                }
                break;

            case "view":
                // view <user>
                if (params.size() >= 2) {
                    List<String> policies = insuranceService.viewPolicies(params.get(1));
                    if (policies == null || policies.isEmpty()) {
                        System.out.println("No active policies found.");
                    } else {
                        policies.forEach(System.out::println);
                    }
                }
                break;

            case "claim":
                // claim <user> <policyId>
                if (params.size() >= 3) {
                    insuranceService.submitClaim(params.get(1), params.get(2));
                } else {
                    System.out.println("Error: Use claim <user> <policyId>");
                }
                break;

            default:
                System.out.println("Unknown action.");
        }
        return null;
    }
}