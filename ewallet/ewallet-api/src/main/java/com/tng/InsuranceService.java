package com.tng;

import java.util.List;

public interface InsuranceService {
    // Purchase Motor Insurance
    void purchaseMotorPolicy(String userId, String phoneNumber, String plateNo);

    // Purchase Travel Insurance
    void purchaseTravelPolicy(String userId, String phoneNumber, String destination, int pax);

    // Submit a claim and return the Claim ID
    String submitClaim(String userId, String policyId);

    // [New] Check the status of a claim using Claim ID
    String getClaimStatus(String claimId);

    // View all active policies for a user
    List<String> viewPolicies(String userId);
}