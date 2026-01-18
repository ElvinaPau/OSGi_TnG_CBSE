package com.tng.payment.impl;

import com.tng.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

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

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    private NotificationService notificationService;

    // --- 1. General Payment Logic ---
    @Override
    public boolean processPayment(String phoneNumber, String username, double amount, String merchant) {
        Wallet wallet = walletService.getWallet(phoneNumber);
        if (wallet == null) {
            System.err.println("No wallet found for phone number: " + phoneNumber);
            return false;
        }
        if (amount <= 0) {
            System.err.println("Invalid payment amount: " + amount);
            return false;
        }
        if (merchant == null || merchant.isBlank()) {
            System.err.println("Invalid merchant name.");
            return false;
        }

        boolean success = walletService.deductBalance(phoneNumber, amount, merchant);
        String status = success ? "SUCCESS" : "FAILED";

        paymentStore.add(new PaymentData(phoneNumber, amount, merchant, "RETAIL", status));
        
        // Notify user
        if (notificationService != null) {
            if (success) {
                notificationService.generateNotification(phoneNumber, "PAYMENT", 
                    "Payment of RM " + String.format("%.2f", amount) + " to " + merchant + " successful");
            } else {
                notificationService.generateNotification(phoneNumber, "PAYMENT", 
                    "Payment of RM " + String.format("%.2f", amount) + " to " + merchant + " failed");
            }
        }
        return success;
    }

    @Override
    public boolean processTopUp(String phoneNumber, String username, double amount) {
        Wallet wallet = walletService.getWallet(phoneNumber);
        if (wallet == null) {
            System.err.println("No wallet found for phone number: " + phoneNumber);
            return false;
        }
        if (amount <= 0) {
            System.err.println("Invalid top-up amount: " + amount);
            return false;
        }

        walletService.addMoney(phoneNumber, amount);
        paymentStore.add(new PaymentData(phoneNumber, amount, "Wallet Top-Up", "TOPUP", "SUCCESS"));
        return true;
    }

    @Override
    public List<PaymentData> getPaymentHistory(String phoneNumber) {
        return paymentStore.stream()
                .filter(p -> p.getUserId().equals(phoneNumber))
                .collect(Collectors.toList());
    }

    // --- 2. QR Logic ---
    @Override
    public boolean processQRPayment(String phoneNumber, String username, String qrString) {
        Wallet wallet = walletService.getWallet(phoneNumber);
        if (wallet == null) {
            System.err.println("No wallet found for phone number: " + phoneNumber);
            return false;
        }
        if (qrString == null || qrString.isBlank()) {
            System.err.println("Invalid QR string.");
            return false;
        }

        String[] parts = qrString.split(":");
        if (parts.length != 2) {
            System.err.println("QR format invalid. Expected: MERCHANT:AMOUNT");
            return false;
        }

        String merchant = parts[0];
        double amount;
        try {
            amount = Double.parseDouble(parts[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid QR amount format.");
            return false;
        }

        if (amount <= 0) {
            System.err.println("Invalid QR payment amount.");
            return false;
        }
        if (merchant == null || merchant.isBlank()) {
            System.err.println("Invalid QR merchant.");
            return false;
        }

        boolean success = walletService.deductBalance(phoneNumber, amount, "QR: " + merchant);
        String status = success ? "SUCCESS" : "FAILED";

        qrStore.add(new QRData(phoneNumber, qrString, merchant, amount, status));
        paymentStore.add(new PaymentData(phoneNumber, amount, "QR: " + merchant, "QR", status));
        
        // Notify user
        if (notificationService != null) {
            if (success) {
                notificationService.generateNotification(phoneNumber, "QR", 
                    "QR Payment of RM " + String.format("%.2f", amount) + " to " + merchant + " successful");
            } else {
                notificationService.generateNotification(phoneNumber, "QR", 
                    "QR Payment of RM " + String.format("%.2f", amount) + " to " + merchant + " failed");
            }
        }
        return success;
    }

    @Override
    public List<QRData> getQRHistory(String phoneNumber) {
        return qrStore.stream()
                .filter(q -> q.getUserId().equals(phoneNumber))
                .collect(Collectors.toList());
    }

    // --- 3. AutoPay Logic ---
    @Override
    public void registerAutoPay(String phoneNumber, String biller, double amount) {
        Wallet wallet = walletService.getWallet(phoneNumber);
        if (wallet == null) {
            System.err.println("No wallet found for phone number: " + phoneNumber);
            return;
        }
        if (amount <= 0) {
            System.err.println("Invalid AutoPay amount: " + amount);
            return;
        }
        if (biller == null || biller.isBlank()) {
            System.err.println("Invalid biller name.");
            return;
        }

        autoPayStore.add(new AutoPayData(phoneNumber, biller, amount, "ACTIVE"));
        System.out.println("AutoPay registered for " + biller);
        
        // Notify user
        if (notificationService != null) {
            notificationService.generateNotification(phoneNumber, "AUTOPAY", 
                "AutoPay setup successful for " + biller + " (RM " + String.format("%.2f", amount) + ")");
        }
    }

    @Override
    public void runAutoPaySimulation(String phoneNumber) {
        Wallet wallet = walletService.getWallet(phoneNumber);
        if (wallet == null) {
            System.err.println("No wallet found for phone number: " + phoneNumber);
            return;
        }

        List<AutoPayData> userAutoPays = getAutoPaySettings(phoneNumber);
        System.out.println("--- Running AutoPay Simulation ---");

        for (AutoPayData data : userAutoPays) {
            if ("ACTIVE".equals(data.getStatus())) {
                boolean success = walletService.deductBalance(phoneNumber, data.getAmount(),
                        "AutoPay: " + data.getBiller());
                String runStatus = success ? "SUCCESS" : "FAILED";

                paymentStore.add(new PaymentData(phoneNumber, data.getAmount(),
                        "AutoPay: " + data.getBiller(), "AUTOPAY", runStatus));

                // Notify user
                if (notificationService != null) {
                    if (success) {
                        notificationService.generateNotification(phoneNumber, "AUTOPAY", 
                            "AutoPay executed for " + data.getBiller() + " (RM " + String.format("%.2f", data.getAmount()) + ")");
                    } else {
                        notificationService.generateNotification(phoneNumber, "AUTOPAY", 
                            "AutoPay failed for " + data.getBiller() + " - Insufficient balance");
                    }
                }

                if (success) {
                    data.updateLastExecuted();
                    System.out.println("Processed AutoPay: " + data);
                } else {
                    System.err.println("Failed AutoPay for " + data.getBiller());
                }
            }
        }
    }

    @Override
    public List<AutoPayData> getAutoPaySettings(String phoneNumber) {
        return autoPayStore.stream()
                .filter(a -> a.getUserId().equals(phoneNumber))
                .collect(Collectors.toList());
    }
}
