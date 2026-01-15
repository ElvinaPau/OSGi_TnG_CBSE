package com.tng.insurance.impl;

import com.tng.InsuranceService;
import com.tng.PaymentService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component(service = InsuranceService.class)
public class InsuranceServiceImpl implements InsuranceService {


    private final Map<String, List<String>> policyStore = new ConcurrentHashMap<>();

    @Reference
    private PaymentService paymentService;

    @Override
    public void purchaseMotorPolicy(String username, String plateNo) {
        System.out.println("\n--- Processing Insurance for " + username + " ---");


        boolean success = paymentService.processPayment(username, 500.0, "Purchase Motor Insurance");

        if (success) {

            String policyInfo = "Motor Policy [" + plateNo + "] - Active";
            policyStore.computeIfAbsent(username, k -> new ArrayList<>()).add(policyInfo);
            System.out.println("✅ Motor Policy Created for car: " + plateNo);
        } else {
            System.out.println("❌ Purchase Failed: Insufficient funds in wallet.");
        }
    }

    @Override
    public void submitClaim(String username, String policyId) {

        System.out.println("✅ Claim submitted for Policy: " + policyId);
        System.out.println("   Status: Pending Review");
    }

    @Override
    public List<String> viewPolicies(String username) {
        return policyStore.getOrDefault(username, new ArrayList<>());
    }
}