package com.tng.insurance.impl;

import com.tng.PaymentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InsuranceServiceImplTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private InsuranceServiceImpl insuranceService;

    @Test
    public void testPurchaseMotorPolicy() {
        // Setup: Mock payment to return true
        when(paymentService.processPayment(anyString(), anyString(), anyDouble(), anyString())).thenReturn(true);

        // Execute
        insuranceService.purchaseMotorPolicy("Ali", "0123456", "W8888");

        // Ensure paymentService was called
        verify(paymentService).processPayment(eq("Ali"), eq("0123456"), eq(500.0), anyString());
    }

    @Test
    public void testSubmitClaim() {
        insuranceService.submitClaim("Ali", "POL-123");
        // Simple verification that it runs without error
    }
}