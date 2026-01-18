package com.tng.investment.impl;

import com.tng.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InvestmentServiceImplTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private InvestmentServiceImpl investmentService;

    private final String TEST_PHONE = "0123456789";
    private final String TEST_USER = "user1";

    @Before
    public void setup() {
        // Initialize sample funds for testing
        investmentService.activate();
    }

    @Test
    public void testInvestInFund_Success() {
        // 1. Setup behavior: Payment is successful
        when(paymentService.processPayment(eq(TEST_PHONE), eq(TEST_USER), eq(100.0), anyString()))
                .thenReturn(true);

        // 2. Execute
        InvestmentData result = investmentService.investInFund(TEST_PHONE, TEST_USER, "F01", 100.0);

        // 3. Verify
        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertEquals(100.0, result.getUnits(), 0.001); // Price of F01 is 1.00
        verify(paymentService).processPayment(eq(TEST_PHONE), eq(TEST_USER), eq(100.0), anyString());
    }

    @Test(expected = RuntimeException.class)
    public void testInvestInFund_InsufficientBalance() {
        // 1. Setup: Payment fails
        when(paymentService.processPayment(anyString(), anyString(), anyDouble(), anyString())).thenReturn(false);

        // 2. Execute (Should throw RuntimeException)
        investmentService.investInFund(TEST_PHONE, TEST_USER, "F02", 1000.0);
    }

    @Test
    public void testRunMarketChangeSimulation() {
        // Get initial price of a fund
        FundData fund = investmentService.getFundById("F01");
        assertNotNull("Fund F01 should exist after activation", fund);
        double oldPrice = fund.getPrice();
        
        // Execute simulation
        investmentService.runMarketChangeSimulation(null);
        
        // Verify price updated
        double newPrice = investmentService.getFundById("F01").getPrice();
        assertNotEquals("Price should change after market simulation", oldPrice, newPrice, 0.00001);
    }
}