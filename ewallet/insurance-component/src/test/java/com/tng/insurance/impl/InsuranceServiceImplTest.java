package com.tng.insurance.impl;

import com.tng.PaymentService;
import org.junit.Before;
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

    @Before
    public void setup() {

    }


    @Test
    public void testPurchaseMotorPolicy_Success() {
        System.out.println("--- Test Case: Purchase Success ---");


        when(paymentService.processPayment(anyString(), anyDouble(), anyString())).thenReturn(true);


        insuranceService.purchaseMotorPolicy("Ali", "W1234");


        verify(paymentService, times(1)).processPayment(eq("Ali"), eq(500.0), anyString());

        System.out.println("Result: Verified that payment was called.");
    }


    @Test
    public void testPurchaseMotorPolicy_PaymentFailed() {
        System.out.println("--- Test Case: Payment Failed ---");


        when(paymentService.processPayment(anyString(), anyDouble(), anyString())).thenReturn(false);


        insuranceService.purchaseMotorPolicy("Bob", "V8888");


        verify(paymentService).processPayment(anyString(), anyDouble(), anyString());
    }


    @Test
    public void testSubmitClaim() {
        System.out.println("--- Test Case: Submit Claim ---");

        insuranceService.submitClaim("Ali", "POL-001");


    }
}