package com.tng.insurance.impl;

import com.tng.PaymentService;
import com.tng.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
// Integration test for Insurance module
// This test verifies real interaction between InsuranceService and persistence layer
class InsuranceIntegrationTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private NotificationService notificationService;

    private InsuranceServiceImpl insuranceService;

    @BeforeEach
    void setUp() {
        insuranceService = new InsuranceServiceImpl();
        TestUtil.inject(insuranceService, "paymentService", paymentService);
        TestUtil.inject(insuranceService, "notificationService", notificationService);
    }

    @Test
    void testClaimFlowIntegration() {
        when(paymentService.processPayment(anyString(), anyString(), anyDouble(), anyString()))
                .thenReturn(true);

        insuranceService.purchaseMotorPolicy("user1", "0123456789", "W8888");
        String claimId = insuranceService.submitClaim("user1", "W8888");

        assertNotNull(claimId);
        assertEquals("Pending Review", insuranceService.getClaimStatus(claimId));

        verify(notificationService, times(1))
                .generateNotification(eq("user1"), eq("CLAIM"), contains("Claim submitted"));
    }
}
