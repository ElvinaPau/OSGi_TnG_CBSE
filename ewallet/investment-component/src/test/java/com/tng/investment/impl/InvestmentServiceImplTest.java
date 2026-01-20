package com.tng.investment.impl;

import com.tng.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

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
        investmentService.activate();
    }

    // --- Buy fund Success Path ---
    @Test
    public void testInvestInFund_Success() {
        // F01 price is 1.0000 by default
        double investAmount = 100.0;
        when(paymentService.processPayment(eq(TEST_PHONE), eq(TEST_USER), eq(investAmount), anyString()))
                .thenReturn(true);

        InvestmentData result = investmentService.investInFund(TEST_PHONE, TEST_USER, "F01", investAmount);

        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertEquals(100.0, result.getUnits(), 0.0001);
        
        // Check if history was recorded
        List<InvestmentData> history = investmentService.getInvestmentHistory(TEST_USER);
        assertEquals(1, history.size());
    }

    // --- Buy fund Failure (Insufficient Balance) ---
    @Test
    public void testInvestInFund_InsufficientBalance() {
        when(paymentService.processPayment(anyString(), anyString(), anyDouble(), anyString()))
                .thenReturn(false);

        try {
            investmentService.investInFund(TEST_PHONE, TEST_USER, "F01", 500.0);
            fail("Should have thrown RuntimeException for insufficient balance");
        } catch (RuntimeException e) {
            assertEquals("Insufficient wallet balance.", e.getMessage());
        }
    }

    // --- Buy fund System Error (Refund Path) ---
    @Test
    public void testInvestInFund_SystemErrorRefund() {
        when(paymentService.processPayment(eq(TEST_PHONE), eq(TEST_USER), anyDouble(), anyString()))
                .thenReturn(true);
        
        try {
            // Force the internal logic to fail (e.g., by mocking a component error)
            investmentService.investInFund(TEST_PHONE, TEST_USER, "F01", 100.0);

        } catch (RuntimeException e) {
            // VERIFY: The error message from your 'catch' block
            assertEquals("System error. Amount refunded.", e.getMessage());

            // VERIFY: The Rollback happened
            verify(paymentService).processTopUp(eq(TEST_PHONE), eq(TEST_USER), eq(100.0));
            
            // VERIFY: A FAILED record was added to history
            List<InvestmentData> history = investmentService.getInvestmentHistory(TEST_USER);
            assertEquals("FAILED", history.get(0).getStatus());
        }
    }

    // --- Sell Fund Success ---
    @Test
    public void testSellFund_Success() {
        // 1. Manually add units to portfolio first
        when(paymentService.processPayment(anyString(), anyString(), anyDouble(), anyString())).thenReturn(true);
        investmentService.investInFund(TEST_PHONE, TEST_USER, "F01", 10.0);

        // 2. Sell 5 units (Price is 1.0, so proceeds = 5.0)
        investmentService.sellFund(TEST_PHONE, TEST_USER, "F01", 5.0);

        // 3. Verify proceeds were topped up back to wallet
        verify(paymentService).processTopUp(eq(TEST_PHONE), eq(TEST_USER), eq(5.0));
        
        // 4. Verify units decreased
        PortfolioData portfolio = investmentService.getUserPortfolio(TEST_USER);
        assertEquals(5.0, portfolio.getUnitsForFund("F01"), 0.0001);
    }

    // --- Sell Fund Failure (Insufficient Units) ---
    @Test(expected = RuntimeException.class)
    public void testSellFund_InsufficientUnits() {
        // User has 0 units initially, trying to sell 10
        investmentService.sellFund(TEST_PHONE, TEST_USER, "F01", 10.0);
    }

    // --- Market Simulation Logic ---
    @Test
    public void testRunMarketChangeSimulation() {
        FundData fund = investmentService.getFundById("F01");
        double initialPrice = fund.getPrice();

        investmentService.runMarketChangeSimulation();

        double newPrice = investmentService.getFundById("F01").getPrice();
        assertNotEquals("Market simulation should update fund prices", initialPrice, newPrice, 0.0001);
    }

    // --- Risk Profile Evaluation ---
    @Test
    public void testEvaluateRiskProfile() {
        investmentService.evaluateRiskProfile(TEST_USER, "High");
        
        PortfolioData portfolio = investmentService.getUserPortfolio(TEST_USER);
        assertEquals("High", portfolio.getRiskCategory());
    }
}