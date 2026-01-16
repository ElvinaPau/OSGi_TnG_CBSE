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

        // Inject dependencies manually
        injectField(walletService, "userService", userService);
        injectField(paymentService, "walletService", walletService);

        // Setup initial state
        userService.findOrCreateUser("0122222222", "Ali");
        walletService.findOrCreateWallet("0122222222", "Ali", 100.0);
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    public void testFullPaymentFlow() {
        // --- Action ---
        // CORRECTED: Pass phoneNumber as first arg, username as second
        boolean success = paymentService.processPayment("0122222222", "Ali", 40.0, "McDonalds");

        // --- Verify Payment ---
        assertTrue("Payment should succeed", success);

        // --- Verify Wallet Balance ---
        // Initial 100 - 40 = 60
        double balance = walletService.getWallet("0122222222").getBalance();
        assertEquals(60.0, balance, 0.01);

        // --- Verify History Logged ---
        // CORRECTED: Retrieve history using phoneNumber (the ID used in paymentStore)
        List<PaymentData> history = paymentService.getPaymentHistory("0122222222");
        assertEquals(1, history.size());

        // Verify description is the merchant name
        assertEquals("McDonalds", history.get(0).getDescription());

        // Verify the Status is SUCCESS
        assertEquals("SUCCESS", history.get(0).getStatus());
    }
}