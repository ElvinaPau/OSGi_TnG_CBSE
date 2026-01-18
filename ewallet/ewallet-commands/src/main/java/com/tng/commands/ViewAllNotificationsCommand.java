package com.tng.commands;

import com.tng.NotificationData;
import com.tng.NotificationService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.List;

@Command(scope = "ewallet", name = "notifications-all", description = "View all notifications")
@Service
public class ViewAllNotificationsCommand implements Action {

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

        List<NotificationData> notifications = notificationService.getAllNotifications(phoneNumber);
        if (notifications.isEmpty()) {
            System.out.println("No notifications found.");
            return null;
        }

        System.out.println("\n=== All Notifications ===");
        for (int i = 0; i < notifications.size(); i++) {
            NotificationData notification = notifications.get(i);
            System.out.println((i + 1) + ". " + notification);
        }

        int unreadCount = notificationService.getUnreadCount(phoneNumber);
        if (unreadCount > 0) {
            System.out.println("\nUnread notifications: " + unreadCount);
        }

        return null;
    }
}
