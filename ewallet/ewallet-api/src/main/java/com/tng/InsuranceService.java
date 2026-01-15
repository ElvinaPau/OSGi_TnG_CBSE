package com.tng;

import java.util.List;

public interface InsuranceService {
    void purchaseMotorPolicy(String userId, String phoneNumber, String plateNo);
    void submitClaim(String username, String policyId);
    List<String> viewPolicies(String username);
}