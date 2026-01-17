package com.tng.payment.impl;

import com.tng.PaymentService;
import com.tng.WalletService;
import com.tng.Wallet; // Import Wallet
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceImplTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Before
    public void setup() {
        // No special setup needed for Mockito runner
    }

    @Test
    public void testProcessPayment_Success() {
        // 1. Define Behavior
        // The service checks if wallet exists, so we must return a mock Wallet
        when(walletService.getWallet(anyString())).thenReturn(mock(Wallet.class));
        // Mock the deduction to return true
        when(walletService.deductBalance(anyString(), anyDouble(), anyString())).thenReturn(true);

        // 2. Execute
        // Corrected Signature: (phoneNumber, username, amount, merchant)
        boolean result = paymentService.processPayment("0122222222", "Ali", 50.0, "Tesco");

        // 3. Verify
        assertTrue(result);
        verify(walletService).deductBalance("0122222222", 50.0, "Tesco");
    }

    @Test
    public void testProcessQRPayment_Success() {
        // 1. Define Behavior
        when(walletService.getWallet(anyString())).thenReturn(mock(Wallet.class));
        when(walletService.deductBalance(anyString(), anyDouble(), anyString())).thenReturn(true);

        // 2. Execute
        // Corrected Signature: (phoneNumber, username, qrString)
        boolean result = paymentService.processQRPayment("0122222222", "Ali", "KFC:25.00");

        // 3. Verify
        assertTrue(result);
        verify(walletService).deductBalance("0122222222", 25.00, "QR: KFC");
    }
}