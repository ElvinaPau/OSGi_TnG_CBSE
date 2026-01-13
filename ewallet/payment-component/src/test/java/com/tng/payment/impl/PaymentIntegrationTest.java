package com.tng.payment.impl;

import com.tng.user.impl.UserServiceImpl;
import com.tng.wallet.impl.WalletServiceImpl;
import com.tng.PaymentData;
import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class PaymentIntegrationTest {

    private PaymentServiceImpl paymentService;
    private WalletServiceImpl walletService;
    private UserServiceImpl userService;

    @Before
    public void setup() throws Exception {
        userService = new UserServiceImpl();
        walletService = new WalletServiceImpl();
        paymentService = new PaymentServiceImpl();

        injectField(walletService, "userService", userService);
        injectField(paymentService, "walletService", walletService);

        userService.findOrCreateUser("Ali");
        walletService.findOrCreateWallet("Ali", 100.0);
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    public void testFullPaymentFlow() {
        // --- Action ---
        boolean success = paymentService.processPayment("Ali", 40.0, "McDonalds");

        // --- Verify Payment ---
        assertTrue("Payment should succeed", success);

        // --- Verify Wallet Balance ---
        double balance = walletService.getWallet("Ali").getBalance();
        assertEquals(60.0, balance, 0.01);

        // --- Verify History Logged ---
        List<PaymentData> history = paymentService.getPaymentHistory("Ali");
        assertEquals(1, history.size());

        // FIX: The description is the merchant name ("McDonalds"), not "RETAIL"
        assertEquals("McDonalds", history.get(0).getDescription()); 
        
        // Verify the Status is SUCCESS
        assertEquals("SUCCESS", history.get(0).getStatus());
    }
}