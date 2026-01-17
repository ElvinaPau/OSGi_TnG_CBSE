package com.tng.insurance.impl;

import com.tng.InsuranceService;
import com.tng.PaymentService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component(service = InsuranceService.class)
public class InsuranceServiceImpl implements InsuranceService {

    // Store policies: UserId -> List of Policy Strings
    private final Map<String, List<String>> policyStore = new ConcurrentHashMap<>();

    // Store claim status: ClaimID -> Status String
    private final Map<String, String> claimStore = new ConcurrentHashMap<>();

    @Reference
    private PaymentService paymentService;

    @Override
    public void purchaseMotorPolicy(String userId, String phoneNumber, String plateNo) {
        String phone = (phoneNumber != null) ? phoneNumber : "N/A";

        // Process payment for Motor Insurance (Fixed amount 500.0)
        boolean success = paymentService.processPayment(userId, phone, 500.0, "Motor Insurance");

        if (success) {
            String info = "ID:" + plateNo + " [Motor] - Active";
            policyStore.computeIfAbsent(userId, k -> new ArrayList<>()).add(info);
            System.out.println("Motor Policy Created: " + plateNo);
        }

    }

    @Override
    public void purchaseTravelPolicy(String userId, String phoneNumber, String destination, int pax) {
        String phone = (phoneNumber != null) ? phoneNumber : "N/A";
        double amount = 80.0 * pax;

        // Process payment for Travel Insurance
        boolean success = paymentService.processPayment(userId, phone, amount, "Travel Insurance");

        if (success) {
            String policyID = "TRV-" + destination.substring(0, 3).toUpperCase();
            String info = "ID:" + policyID + " [Travel to " + destination + "] - Active";
            policyStore.computeIfAbsent(userId, k -> new ArrayList<>()).add(info);
            System.out.println("Travel Policy Created: " + policyID);
        }
    }

    @Override
    public String submitClaim(String userId, String policyId) {
        // Generate a unique Claim ID
        String claimId = "CLM-" + System.currentTimeMillis();

        // Store the status as 'Pending Review'
        claimStore.put(claimId, "Pending Review");

        System.out.println("Claim submitted for Policy: " + policyId);
        System.out.println("Claim ID: " + claimId);

        return claimId;
    }

    @Override
    public String getClaimStatus(String claimId) {
        return claimStore.getOrDefault(claimId, "Not Found");
    }

    @Override
    public List<String> viewPolicies(String userId) {
        return policyStore.getOrDefault(userId, new ArrayList<>());
    }
}