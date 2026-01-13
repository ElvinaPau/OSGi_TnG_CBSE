package com.tng.payment.impl;

import com.tng.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
        
        // Log with status
        String status = success ? "SUCCESS" : "FAILED";
        
        // Only saving SUCCESS for now, but you could save FAILED too
        if (success) {
            // FIXED: Added 'status' argument
            paymentStore.add(new PaymentData(userId, amount, merchant, "RETAIL", status));
        }
        return success;
    }

    @Override
    public boolean processTopUp(String userId, double amount) {
        walletService.addFunds(userId, amount); 
        // FIXED: Added "SUCCESS" status
        paymentStore.add(new PaymentData(userId, amount, "Wallet Top-Up", "TOPUP", "SUCCESS"));
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
        String[] parts = qrString.split(":");
        if (parts.length != 2) return false;

        String merchant = parts[0];
        double amount = Double.parseDouble(parts[1]);

        boolean success = walletService.deductBalance(userId, amount, "QR: " + merchant);
        String status = success ? "SUCCESS" : "FAILED";

        if (success) {
            // FIXED: Added 'status' argument to both
            qrStore.add(new QRData(userId, qrString, merchant, amount, status));
            paymentStore.add(new PaymentData(userId, amount, "QR: " + merchant, "QR", status));
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
        // FIXED: Added "ACTIVE" status
        autoPayStore.add(new AutoPayData(userId, biller, amount, "ACTIVE"));
        System.out.println("AutoPay registered for " + biller);
    }

    @Override
    public void runAutoPaySimulation(String userId) {
        List<AutoPayData> userAutoPays = getAutoPaySettings(userId);
        
        System.out.println("--- Running AutoPay Simulation ---");
        for (AutoPayData data : userAutoPays) {
            // Check status properly
            if ("ACTIVE".equals(data.getStatus())) {
                boolean success = walletService.deductBalance(userId, data.getAmount(), "AutoPay: " + data.getBiller());
                
                String runStatus = success ? "SUCCESS" : "FAILED";
                
                if (success) {
                    data.updateLastExecuted();
                    // FIXED: Added 'runStatus' argument
                    paymentStore.add(new PaymentData(userId, data.getAmount(), "AutoPay: " + data.getBiller(), "AUTOPAY", runStatus));
                    System.out.println("Processed AutoPay: " + data);
                } else {
                    System.err.println("Failed AutoPay for " + data.getBiller());
                }
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