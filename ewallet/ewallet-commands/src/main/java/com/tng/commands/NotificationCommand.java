package com.tng.commands;

import com.tng.NotificationService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "notifications", description = "Check unread notification count")
@Service
public class NotificationCommand implements Action {

    @Reference
    private NotificationService notificationService;

    @Argument(index = 0, name = "phoneNumber", description = "User's phone number", required = true)
    private String phoneNumber;

    @Override
    public Object execute() throws Exception {
        if (notificationService == null) {
            System.err.println("ERROR: NotificationService not available!");
            return null;
        }

        int unreadCount = notificationService.getUnreadCount(phoneNumber);
        System.out.println("Unread notifications: " + unreadCount);
        System.out.println("\nAvailable commands:");
        System.out.println("  ewallet:notifications-all <phoneNumber>     - View all notifications");
        System.out.println("  ewallet:notifications-unread <phoneNumber>  - View unread notifications");
        System.out.println("  ewallet:notifications-read <phoneNumber>    - Mark all notifications as read");

        return null;
    }
}
