package com.tng.commands;

import com.tng.NotificationData;
import com.tng.NotificationService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.List;

@Command(scope = "ewallet", name = "notifications-unread", description = "View unread notifications")
@Service
public class ViewUnreadNotificationsCommand implements Action {

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

        List<NotificationData> unreadNotifications = notificationService.getUnreadNotifications(phoneNumber);
        if (unreadNotifications.isEmpty()) {
            System.out.println("No unread notifications.");
            return null;
        }

        System.out.println("\n=== Unread Notifications ===");
        for (int i = 0; i < unreadNotifications.size(); i++) {
            NotificationData notification = unreadNotifications.get(i);
            System.out.println((i + 1) + ". " + notification);
        }

        System.out.println("\nTotal unread: " + unreadNotifications.size());

        return null;
    }
}
