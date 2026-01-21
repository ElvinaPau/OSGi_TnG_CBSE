package com.tng.insurance.impl;

import com.tng.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
// Unit test for InsuranceServiceImpl
// This test verifies the business logic of the Insurance component
class InsuranceServiceImplTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private InsuranceServiceImpl insuranceService;

    @Test
    void testPurchaseMotorPolicy() {
        when(paymentService.processPayment(anyString(), anyString(), eq(500.0), anyString()))
                .thenReturn(true);

        insuranceService.purchaseMotorPolicy("user1", "0123456789", "W1234");

        verify(paymentService, times(1))
                .processPayment(eq("user1"), eq("0123456789"), eq(500.0), contains("Motor"));
    }

    @Test
    void testPurchaseTravelPolicy() {
        when(paymentService.processPayment(anyString(), anyString(), eq(160.0), anyString()))
                .thenReturn(true);

        insuranceService.purchaseTravelPolicy("user1", "0123456789", "Japan", 2);

        verify(paymentService, times(1))
                .processPayment(eq("user1"), eq("0123456789"), eq(160.0), contains("Travel"));
    }
}
