// package com.tng.payment.impl;

// import com.tng.PaymentService;
// import com.tng.WalletService;
// import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.MockitoJUnitRunner;

// import java.lang.reflect.Field;

// import static org.junit.Assert.assertTrue;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// @RunWith(MockitoJUnitRunner.class)
// public class PaymentServiceImplTest {

//     @Mock
//     private WalletService walletService;

//     @InjectMocks
//     private PaymentServiceImpl paymentService;

//     @Before
//     public void setup() {
//         // Since we are not using a DI container like Spring, @InjectMocks might need help 
//         // if fields are private. But usually, Mockito handles it.
//     }

//     @Test
//     public void testProcessPayment_Success() {
//         // 1. Define Behavior: Wallet returns true (deduction successful)
//         when(walletService.deductBalance(anyString(), anyDouble(), anyString())).thenReturn(true);

//         // 2. Execute
//         boolean result = paymentService.processPayment("0122222222", 50.0, "Tesco");

//         // 3. Verify
//         assertTrue(result);
//         verify(walletService).deductBalance("0122222222", 50.0, "Tesco");
//     }

//     @Test
//     public void testProcessQRPayment_Success() {
//         // 1. Define Behavior
//         when(walletService.deductBalance(anyString(), anyDouble(), anyString())).thenReturn(true);

//         // 2. Execute
//         boolean result = paymentService.processQRPayment("0122222222", "KFC:25.00");

//         // 3. Verify
//         assertTrue(result);
//         verify(walletService).deductBalance("0122222222", 25.00, "QR: KFC");
//     }
// }