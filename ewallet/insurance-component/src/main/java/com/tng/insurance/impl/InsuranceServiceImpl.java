package com.tng.insurance.impl;

import com.tng.InsuranceService;
import com.tng.PaymentService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component(service = InsuranceService.class)
public class InsuranceServiceImpl implements InsuranceService {

    private final Map<String, List<String>> policyStore = new ConcurrentHashMap<>();

    @Reference
    private PaymentService paymentService;

    @Override
    public void purchaseMotorPolicy(String userId, String phoneNumber, String plateNo) {
        String phone = (phoneNumber != null) ? phoneNumber : "N/A";

        // Process payment
        boolean success = paymentService.processPayment(userId, phone, 500.0, "Motor Insurance");

        if (success) {
            // Create and store policy
            String policyInfo = "ID:" + plateNo + " [Motor] - Active";
            policyStore.computeIfAbsent(userId, k -> new ArrayList<>()).add(policyInfo);
            System.out.println("Motor Policy Created: " + plateNo);
        } else {
            System.err.println("Purchase Failed: Insufficient funds.");
        }
    }

    @Override
    public void submitClaim(String userId, String policyId) {
        // Log claim submission
        System.out.println("Claim submitted for Policy: " + policyId + " (User: " + userId + ")");
    }

    @Override
    public List<String> viewPolicies(String userId) {
        return policyStore.getOrDefault(userId, new ArrayList<>());
    }
}