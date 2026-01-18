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
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class InsuranceServiceImplTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private InsuranceServiceImpl insuranceService;

    @Test
    public void testPurchaseMotorPolicy() {
        // Arrange: Mock PaymentService to return true
        // Expects: (phone, username, amount, description)
        when(paymentService.processPayment(anyString(), anyString(), eq(500.0), anyString())).thenReturn(true);

        // Act
        insuranceService.purchaseMotorPolicy("0123456789", "Ali", "W1234");

        // Assert: Verify payment was called with correct parameters
        verify(paymentService, times(1)).processPayment(eq("0123456789"), eq("Ali"), eq(500.0), contains("Motor"));
    }

    @Test
    public void testPurchaseTravelPolicy() {
        // Arrange
        // Travel cost: 2 pax * 80.0 = 160.0
        when(paymentService.processPayment(anyString(), anyString(), eq(160.0), anyString())).thenReturn(true);

        // Act
        insuranceService.purchaseTravelPolicy("0123456789", "Ali", "Japan", 2);

        // Assert
        verify(paymentService, times(1)).processPayment(eq("0123456789"), eq("Ali"), eq(160.0), contains("Travel"));
    }
}