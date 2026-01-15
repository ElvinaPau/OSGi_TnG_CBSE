package com.tng.commands;

import com.tng.PaymentService;
import com.tng.User;
import com.tng.UserService;
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

    @Reference
    private UserService userService;

    @Argument(index = 0, name = "phoneNumber", description = "User Phone Number", required = true, multiValued = false)
    private String phoneNumber;

    @Argument(index = 1, name = "qrString", description = "QR String (Format: Merchant:Amount)", required = true, multiValued = false)
    private String qrString;

    @Override
    public Object execute() throws Exception {
        if (paymentService == null) {
            System.err.println("Error: PaymentService is not available.");
            return null;
        }

        User user = userService.getUser(phoneNumber);
        String username = (user != null) ? user.getUsername() : phoneNumber;

        System.out.println("Scanning QR Code: " + qrString);
        boolean success = paymentService.processQRPayment(phoneNumber, username, qrString);

        if (success) {
            System.out.printf("QR Payment Successful! %s (%s) paid via QR%n", username, phoneNumber);
        } else {
            System.err.println("QR Payment Failed. Check balance, format (Merchant:Amount), or phone number.");
        }
        return null;
    }
}