package com.tng;

import java.util.List;

public interface PaymentService {
    // 1. General Payment Data
    boolean processPayment(String userId, double amount, String merchant);
    boolean processTopUp(String userId, double amount); // Handles TopUp + Logging
    List<PaymentData> getPaymentHistory(String userId);

    // 2. QR Data
    boolean processQRPayment(String userId, String qrString);
    List<QRData> getQRHistory(String userId);

    // 3. AutoPay Data
    void registerAutoPay(String userId, String biller, double amount);
    void runAutoPaySimulation(String userId);
    List<AutoPayData> getAutoPaySettings(String userId);
}