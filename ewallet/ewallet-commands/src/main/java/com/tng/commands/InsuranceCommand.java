package com.tng.commands;

import com.tng.InsuranceService;
import org.apache.karaf.shell.api.action.*;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import java.util.List;

@Command(scope = "ewallet", name = "insurance", description = "Manage Insurance Operations")
@Service
public class InsuranceCommand implements Action {

    @Reference
    private InsuranceService insuranceService;

    @Argument(index = 0, name = "params", multiValued = true, description = "Command Arguments")
    private List<String> params;

    @Override
    public Object execute() throws Exception {
        if (params == null || params.isEmpty()) {
            System.out.println("Usage: insurance [buy | buy-travel | view | claim | check-status] [args...]");
            return null;
        }

        String action = params.get(0);

        // 1. Buy Motor Insurance
        // Usage: insurance buy <user> <phone> <plate>
        if ("buy".equals(action)) {
            if (params.size() >= 4) {
                insuranceService.purchaseMotorPolicy(params.get(1), params.get(2), params.get(3));
            } else {
                System.out.println("Error: Usage -> insurance buy <user> <phone> <plate>");
            }
        }
        // 2. Buy Travel Insurance
        // Usage: insurance buy-travel <user> <phone> <dest> <pax>
        else if ("buy-travel".equals(action)) {
            if (params.size() >= 5) {
                try {
                    int pax = Integer.parseInt(params.get(4));
                    insuranceService.purchaseTravelPolicy(params.get(1), params.get(2), params.get(3), pax);
                } catch (NumberFormatException e) {
                    System.out.println("Error: Pax must be a number.");
                }
            } else {
                System.out.println("Error: Usage -> insurance buy-travel <user> <phone> <dest> <pax>");
            }
        }
        // 3. View Policies
        // Usage: insurance view <user>
        else if ("view".equals(action)) {
            if (params.size() >= 2) {
                List<String> policies = insuranceService.viewPolicies(params.get(1));
                if (policies != null && !policies.isEmpty()) {
                    policies.forEach(System.out::println);
                } else {
                    System.out.println("No policies found.");
                }
            } else {
                System.out.println("Error: Usage -> insurance view <user>");
            }
        }
        // 4. Submit Claim
        // Usage: insurance claim <user> <policyId>
        else if ("claim".equals(action)) {
            if (params.size() >= 3) {
                String claimId = insuranceService.submitClaim(params.get(1), params.get(2));
                System.out.println("Please copy Claim ID to check status: " + claimId);
            } else {
                System.out.println("Error: Usage -> insurance claim <user> <policyId>");
            }
        }
        // 5. Check Claim Status (New Feature)
        // Usage: insurance check-status <claimId>
        else if ("check-status".equals(action)) {
            if (params.size() >= 2) {
                String status = insuranceService.getClaimStatus(params.get(1));
                System.out.println("Claim Status: " + status);
            } else {
                System.out.println("Error: Usage -> insurance check-status <claimId>");
            }
        }
        else {
            System.out.println("Unknown command. Available: buy, buy-travel, view, claim, check-status");
        }
        return null;
    }
}