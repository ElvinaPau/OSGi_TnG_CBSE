package com.tng.commands;

import com.tng.PaymentService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "scan-qr", description = "Scan and Pay via QR String")
@Service
public class ScanQRCommand implements Action {

    @Reference
    private PaymentService paymentService;

    @Argument(index = 0, name = "username", description = "User ID", required = true, multiValued = false)
    private String username;

    @Argument(index = 1, name = "qrString", description = "QR String (Format: Merchant:Amount)", required = true, multiValued = false)
    private String qrString;

    @Override
    public Object execute() throws Exception {
        if (paymentService == null) {
            System.err.println("Error: PaymentService is not available.");
            return null;
        }

        System.out.println("Scanning QR Code: " + qrString);
        boolean success = paymentService.processQRPayment(username, qrString);

        if (success) {
            System.out.println("QR Payment Successful!");
        } else {
            System.err.println("QR Payment Failed. Check balance, format (Merchant:Amount), or user ID.");
        }
        return null;
    }
}