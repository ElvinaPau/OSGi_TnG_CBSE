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

    @Before
    public void setup() {
        // Initialize sample funds for testing
        investmentService.activate();
    }

    @Test
    public void testInvestInFund_Success() {
        // 1. Setup behavior: Payment is successful
        when(paymentService.processPayment(eq("user1"), eq(100.0), anyString())).thenReturn(true);

        // 2. Execute
        InvestmentData result = investmentService.investInFund("user1", "F01", 100.0);

        // 3. Verify
        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertEquals(100.0, result.getUnits(), 0.001); // Price of F01 is 1.00
        verify(paymentService).processPayment(eq("user1"), eq(100.0), anyString());
    }

    @Test(expected = RuntimeException.class)
    public void testInvestInFund_InsufficientBalance() {
        // 1. Setup: Payment fails
        when(paymentService.processPayment(anyString(), anyDouble(), anyString())).thenReturn(false);

        // 2. Execute (Should throw RuntimeException)
        investmentService.investInFund("user1", "F01", 1000.0);
    }

    @Test
    public void testRunMarketChangeSimulation() {
        // Get initial price
        double oldPrice = investmentService.getFundById("F01").getPrice();
        
        // Execute simulation
        investmentService.runMarketChangeSimulation();
        
        // Verify price updated
        double newPrice = investmentService.getFundById("F01").getPrice();
        assertNotEquals(oldPrice, newPrice, 0.00001);
    }
}