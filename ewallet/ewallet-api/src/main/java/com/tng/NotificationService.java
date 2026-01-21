package com.tng;

import java.util.List;

public interface NotificationService {
    /**
     * Generate and save a notification
     * @param phoneNumber User's phone number for lookup
     * @param type Notification type (PAYMENT, WALLET, INVESTMENT, AUTOPAY, QR, CLAIM)
     * @param message Notification message
     */
    void generateNotification(String phoneNumber, String type, String message);

    /**
     * Get all notifications for a user (ordered by timestamp ascending)
     * @param phoneNumber User's phone number
     * @return List of all notifications
     */
    List<NotificationData> getAllNotifications(String phoneNumber);

    /**
     * Get only unread notifications for a user (ordered by timestamp descending)
     * @param phoneNumber User's phone number
     * @return List of unread notifications
     */
    List<NotificationData> getUnreadNotifications(String phoneNumber);

    /**
     * Get count of unread notifications
     * @param phoneNumber User's phone number
     * @return Number of unread notifications
     */
    int getUnreadCount(String phoneNumber);

    /**
     * Mark a single notification as read
     * @param notificationId Notification ID
     */
    void markAsRead(String notificationId);

    /**
     * Mark all notifications as read for a user
     * @param phoneNumber User's phone number
     */
    void markAllAsRead(String phoneNumber);

    /**
     * Display all notifications in console format
     * @param phoneNumber User's phone number
     */
    void displayNotifications(String phoneNumber);

}
