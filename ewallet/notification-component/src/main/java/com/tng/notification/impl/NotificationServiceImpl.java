package com.tng.notification.impl;

import com.tng.NotificationData;
import com.tng.NotificationService;
import com.tng.UserService;
import com.tng.WalletService;
import com.tng.PaymentService;
import com.tng.InvestmentService;
import com.tng.InsuranceService;
import com.tng.User;
import com.tng.Wallet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component(service = NotificationService.class)
public class NotificationServiceImpl implements NotificationService {

    // In-memory store: phoneNumber -> List of notifications
    private final Map<String, List<NotificationData>> notificationStore = new ConcurrentHashMap<>();

    @Reference
    private UserService userService;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    private WalletService walletService;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    private PaymentService paymentService;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    private InvestmentService investmentService;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    private InsuranceService insuranceService;

    @Override
    public void generateNotification(String phoneNumber, String type, String message) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            System.err.println("Phone number cannot be null or empty");
            return;
        }

        User user = userService.getUser(phoneNumber);
        if (user == null) {
            System.err.println("User not found for phone number: " + phoneNumber);
            return;
        }

        NotificationData notification = new NotificationData(
                user.getId(),
                user.getUsername(),
                phoneNumber,
                type,
                message
        );

        notificationStore.computeIfAbsent(phoneNumber, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(notification);

        System.out.println("[NOTIFICATION] " + message);
    }

    @Override
    public List<NotificationData> getAllNotifications(String phoneNumber) {
        List<NotificationData> notifications = notificationStore.getOrDefault(phoneNumber, new ArrayList<>());
        return notifications.stream()
                .sorted(Comparator.comparing(NotificationData::getTimestamp))
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationData> getUnreadNotifications(String phoneNumber) {
        List<NotificationData> notifications = notificationStore.getOrDefault(phoneNumber, new ArrayList<>());
        return notifications.stream()
                .filter(n -> !n.isRead())
                .sorted(Comparator.comparing(NotificationData::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public int getUnreadCount(String phoneNumber) {
        List<NotificationData> notifications = notificationStore.getOrDefault(phoneNumber, new ArrayList<>());
        return (int) notifications.stream()
                .filter(n -> !n.isRead())
                .count();
    }

    @Override
    public void markAsRead(String notificationId) {
        for (List<NotificationData> notifications : notificationStore.values()) {
            for (NotificationData notification : notifications) {
                if (notification.getId().equals(notificationId)) {
                    notification.setRead(true);
                    return;
                }
            }
        }
    }

    @Override
    public void markAllAsRead(String phoneNumber) {
        List<NotificationData> notifications = notificationStore.getOrDefault(phoneNumber, new ArrayList<>());
        notifications.forEach(n -> n.setRead(true));
    }

    @Override
    public void displayNotifications(String phoneNumber) {
        List<NotificationData> notifications = getAllNotifications(phoneNumber);
        if (notifications.isEmpty()) {
            System.out.println("No notifications found.");
            return;
        }

        System.out.println("\n=== All Notifications ===");
        for (NotificationData notification : notifications) {
            System.out.println(notification);
        }

        List<NotificationData> unread = getUnreadNotifications(phoneNumber);
        if (!unread.isEmpty()) {
            System.out.println("\nMark all as read? (Y/N)");
        }
    }


}
