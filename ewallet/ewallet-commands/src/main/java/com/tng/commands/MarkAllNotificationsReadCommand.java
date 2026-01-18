package com.tng.commands;

import com.tng.NotificationService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "notifications-read", description = "Mark all notifications as read")
@Service
public class MarkAllNotificationsReadCommand implements Action {

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

        notificationService.markAllAsRead(phoneNumber);
        System.out.println("All notifications marked as read for: " + phoneNumber);

        return null;
    }
}
