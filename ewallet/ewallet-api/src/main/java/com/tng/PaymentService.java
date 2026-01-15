package com.tng;

import java.util.List;

public interface PaymentService {
    // 1. General Payment Data
    boolean processPayment(String phoneNumber, String username, double amount, String merchant);
    boolean processTopUp(String phoneNumber, String username, double amount);
    List<PaymentData> getPaymentHistory(String phoneNumber);

    // 2. QR Data
    boolean processQRPayment(String phoneNumber, String username, String qrString);
    List<QRData> getQRHistory(String phoneNumber);

    // 3. AutoPay Data
    void registerAutoPay(String phoneNumber, String biller, double amount);
    void runAutoPaySimulation(String phoneNumber);
    List<AutoPayData> getAutoPaySettings(String phoneNumber);
}