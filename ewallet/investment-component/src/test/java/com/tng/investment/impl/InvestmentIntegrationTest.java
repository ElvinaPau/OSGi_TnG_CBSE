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

    @Before
    public void setup() throws Exception {
        userService = new UserServiceImpl();
        walletService = new WalletServiceImpl();
        paymentService = new PaymentServiceImpl();
        investmentService = new InvestmentServiceImpl();

        // Manual Injection
        injectField(walletService, "userService", userService);
        injectField(paymentService, "walletService", walletService);
        injectField(investmentService, "paymentService", paymentService);

        // Setup environment
        investmentService.activate(); // Create funds
        userService.findOrCreateUser("user1");
        walletService.findOrCreateWallet("user1", 1000.0); // Start with RM 1000
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    public void testFullInvestmentAndSellFlow() {
        String userId = "user1";
        String fundId = "F02";
        double investAmount = 500.0;

        // 1. BUY
        InvestmentData buyRecord = investmentService.investInFund(userId, fundId, investAmount);
        
        assertEquals(200.0, buyRecord.getUnits(), 0.01);
        assertEquals(500.0, walletService.getWallet(userId).getBalance(), 0.01);

        // 2. CHECK PORTFOLIO
        PortfolioData portfolio = investmentService.getUserPortfolio(userId);
        assertEquals(200.0, portfolio.getUnitsForFund(fundId), 0.01);

        // 3. SELL
        investmentService.sellFund(userId, fundId, 100.0);

        assertEquals(750.0, walletService.getWallet(userId).getBalance(), 0.01);
        assertEquals(100.0, investmentService.getUserPortfolio(userId).getUnitsForFund(fundId), 0.01);

        // 4. VERIFY HISTORY
        List<InvestmentData> history = investmentService.getInvestmentHistory(userId);
        assertEquals(2, history.size()); // 1 BUY, 1 SELL
    }
}