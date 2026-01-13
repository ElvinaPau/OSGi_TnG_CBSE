package com.tng.commands;

import com.tng.AutoPayData;
import com.tng.PaymentData;
import com.tng.PaymentService;
import com.tng.QRData;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.List;

@Command(scope = "ewallet", name = "history", description = "View transaction history (general/qr/autopay)")
@Service
public class HistoryCommand implements Action {

    @Reference
    private PaymentService paymentService;

    @Argument(index = 0, name = "username", description = "User ID", required = true, multiValued = false)
    private String username;

    @Argument(index = 1, name = "type", description = "Type: 'general' (default), 'qr', or 'autopay'", required = false, multiValued = false)
    private String type;

    @Override
    public Object execute() throws Exception {
        if (paymentService == null) {
            System.err.println("Error: PaymentService is not available.");
            return null;
        }

        // Default to "general" if type is null
        String viewType = (type == null) ? "general" : type.toLowerCase();

        System.out.println("=== " + viewType.toUpperCase() + " HISTORY for " + username + " ===");

        switch (viewType) {
            case "qr":
                List<QRData> qrHistory = paymentService.getQRHistory(username);
                if (qrHistory.isEmpty()) System.out.println("No QR transactions found.");
                qrHistory.forEach(System.out::println);
                break;

            case "autopay":
                List<AutoPayData> autoPaySettings = paymentService.getAutoPaySettings(username);
                if (autoPaySettings.isEmpty()) System.out.println("No AutoPay settings configured.");
                autoPaySettings.forEach(System.out::println);
                break;

            case "general":
            default:
                List<PaymentData> payments = paymentService.getPaymentHistory(username);
                if (payments.isEmpty()) System.out.println("No payment records found.");
                payments.forEach(System.out::println);
                break;
        }

        return null;
    }
}