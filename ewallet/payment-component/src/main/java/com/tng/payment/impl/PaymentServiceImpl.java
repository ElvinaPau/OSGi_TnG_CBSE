package com.tng.payment.impl;

import com.tng.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component(service = PaymentService.class)
public class PaymentServiceImpl implements PaymentService {

    // 3 Separate Data Stores (In-Memory)
    private final List<PaymentData> paymentStore = new CopyOnWriteArrayList<>();
    private final List<QRData> qrStore = new CopyOnWriteArrayList<>();
    private final List<AutoPayData> autoPayStore = new CopyOnWriteArrayList<>();

    @Reference
    private WalletService walletService;

    // --- 1. General Payment Logic ---
    @Override
    public boolean processPayment(String userId, double amount, String merchant) {
        boolean success = walletService.deductBalance(userId, amount, merchant);
        if (success) {
            paymentStore.add(new PaymentData(userId, amount, merchant, "RETAIL"));
        }
        return success;
    }

    @Override
    public boolean processTopUp(String userId, double amount) {
        // We assume WalletService has an 'addFunds' method. 
        // If it returns void, we just assume success for simplicity here.
        walletService.addFunds(userId, amount); 
        paymentStore.add(new PaymentData(userId, amount, "Wallet Top-Up", "TOPUP"));
        return true;
    }

    @Override
    public List<PaymentData> getPaymentHistory(String userId) {
        return paymentStore.stream()
                .filter(p -> p.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    // --- 2. QR Logic ---
    @Override
    public boolean processQRPayment(String userId, String qrString) {
        // Parse "Merchant:Amount" e.g., "McDonalds:15.50"
        String[] parts = qrString.split(":");
        if (parts.length != 2) {
            System.err.println("Invalid QR Format. Use 'Merchant:Amount'");
            return false;
        }

        String merchant = parts[0];
        double amount;
        try {
            amount = Double.parseDouble(parts[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid Amount in QR");
            return false;
        }

        boolean success = walletService.deductBalance(userId, amount, "QR: " + merchant);
        if (success) {
            qrStore.add(new QRData(userId, qrString, merchant, amount));
            paymentStore.add(new PaymentData(userId, amount, "QR: " + merchant, "QR"));
        }
        return success;
    }

    @Override
    public List<QRData> getQRHistory(String userId) {
        return qrStore.stream()
                .filter(q -> q.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    // --- 3. AutoPay Logic ---
    @Override
    public void registerAutoPay(String userId, String biller, double amount) {
        autoPayStore.add(new AutoPayData(userId, biller, amount));
        System.out.println("AutoPay registered for " + biller);
    }

    @Override
    public void runAutoPaySimulation(String userId) {
        List<AutoPayData> userAutoPays = getAutoPaySettings(userId);
        if (userAutoPays.isEmpty()) {
            System.out.println("No AutoPay settings found for user.");
            return;
        }

        System.out.println("--- Running AutoPay Simulation ---");
        for (AutoPayData data : userAutoPays) {
            boolean success = walletService.deductBalance(userId, data.getAmount(), "AutoPay: " + data.getBiller());
            if (success) {
                data.updateLastExecuted();
                // Also log to general history
                paymentStore.add(new PaymentData(userId, data.getAmount(), "AutoPay: " + data.getBiller(), "AUTOPAY"));
                System.out.println("Processed AutoPay: " + data);
            } else {
                System.err.println("Failed AutoPay for " + data.getBiller() + " (Insufficient Funds)");
            }
        }
    }

    @Override
    public List<AutoPayData> getAutoPaySettings(String userId) {
        return autoPayStore.stream()
                .filter(a -> a.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}