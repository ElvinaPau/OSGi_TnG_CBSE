package com.tng.investment.impl;

import com.tng.payment.impl.PaymentServiceImpl;
import com.tng.wallet.impl.WalletServiceImpl;
import com.tng.user.impl.UserServiceImpl;
import com.tng.*;

import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class InvestmentIntegrationTest {

    private InvestmentServiceImpl investmentService;
    private PaymentServiceImpl paymentService;
    private WalletServiceImpl walletService;
    private UserServiceImpl userService;

    private final String PHONE = "0123456789";
    private final String USER = "user1";

    @Before
    public void setup() throws Exception {
        // Initialize real implementations
        userService = new UserServiceImpl();
        walletService = new WalletServiceImpl();
        paymentService = new PaymentServiceImpl();
        investmentService = new InvestmentServiceImpl();

        // Manual Dependency Injection via Reflection
        injectField(walletService, "userService", userService);
        injectField(paymentService, "walletService", walletService);
        injectField(investmentService, "paymentService", paymentService);

        // Reset state and activate funds
        investmentService.activate(); 
        
        // Prepare User and Initial Wallet State
        userService.findOrCreateUser(PHONE, USER);
        walletService.findOrCreateWallet(PHONE, USER, 1000.0);
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    public void testCompleteInvestmentLifecycle() {
        // --- PHASE 1: BUY ---
        double investAmount = 500.0;
        InvestmentData buyRecord = investmentService.investInFund(PHONE, USER, "F02", investAmount);
        
        assertNotNull("Investment record should be generated", buyRecord);
        assertEquals(200.0, buyRecord.getUnits(), 0.001);
        assertEquals(500.0, walletService.getWallet(PHONE).getBalance(), 0.001);

        // --- PHASE 2: MARKET SIMULATION & RETURNS ---
        investmentService.updateFundPrice("F02", 3.0000);
        
        // Net Returns calculation: (Current Value: 200 * 3.0 = 600) - (Investment: 500) = 100 profit
        double returns = investmentService.calculateReturns(USER);
        assertEquals(100.0, returns, 0.001);

        // --- PHASE 3: SELL ---
        investmentService.sellFund(PHONE, USER, "F02", 100.0);

        assertEquals(800.0, walletService.getWallet(PHONE).getBalance(), 0.001);
        assertEquals(100.0, investmentService.getUserPortfolio(USER).getUnitsForFund("F02"), 0.001);

        // --- PHASE 4: RISK ASSESSMENT ---
        investmentService.evaluateRiskProfile(USER, "High");
        assertEquals("High", investmentService.getUserPortfolio(USER).getRiskCategory());

        // --- PHASE 5: HISTORY INTEGRITY ---
        List<InvestmentData> history = investmentService.getInvestmentHistory(USER);
        assertEquals(2, history.size());
        assertEquals("BUY", history.get(0).getType());
        assertEquals("SELL", history.get(1).getType());
    }
}