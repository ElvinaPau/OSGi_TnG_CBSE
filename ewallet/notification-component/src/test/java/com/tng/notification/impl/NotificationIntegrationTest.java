package com.tng.notification.impl;

import com.tng.NotificationData;
import com.tng.NotificationService;
import com.tng.User;
import com.tng.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("NotificationService Integration Tests")
class NotificationIntegrationTest {

    private NotificationServiceImpl notificationService;

    @Mock
    private UserService userService;

    private User testUser;
    private String testPhoneNumber = "0123456789";
    private String testUsername = "IntegrationTestUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationServiceImpl();

        // Inject mocked dependencies
        setPrivateField(notificationService, "userService", userService);

        // Create test user
        testUser = new User(testUsername, testPhoneNumber);

        // Mock services
        when(userService.getUser(testPhoneNumber)).thenReturn(testUser);
    }

    @Test
    @DisplayName("Multi-transaction flow with different notification types")
    void testMultipleTransactionTypesFlow() {
        // Scenario: Multiple transaction types generate different notification types

        // Generate WALLET notification (add money)
        notificationService.generateNotification(testPhoneNumber, "WALLET", "RM 1000 added to wallet");

        // Generate PAYMENT notification
        notificationService.generateNotification(testPhoneNumber, "PAYMENT", "Payment to Netflix RM 54.99");

        // Generate AUTOPAY notification
        notificationService.generateNotification(testPhoneNumber, "AUTOPAY", "AutoPay registered for Electricity Bill");

        // Generate INVESTMENT notification
        notificationService.generateNotification(testPhoneNumber, "INVESTMENT", "Investment in ASB Fund: 10 units purchased");

        // Generate CLAIM notification
        notificationService.generateNotification(testPhoneNumber, "CLAIM", "Insurance Claim submitted - Claim ID: CLM123456");

        // Verify all 5 notifications generated
        List<NotificationData> allNotifications = notificationService.getAllNotifications(testPhoneNumber);
        assertEquals(5, allNotifications.size(), "Should have 5 notifications of different types");

        // Verify each type exists
        boolean hasWallet = allNotifications.stream().anyMatch(n -> "WALLET".equals(n.getType()));
        boolean hasPayment = allNotifications.stream().anyMatch(n -> "PAYMENT".equals(n.getType()));
        boolean hasAutoPay = allNotifications.stream().anyMatch(n -> "AUTOPAY".equals(n.getType()));
        boolean hasInvestment = allNotifications.stream().anyMatch(n -> "INVESTMENT".equals(n.getType()));
        boolean hasClaim = allNotifications.stream().anyMatch(n -> "CLAIM".equals(n.getType()));

        assertTrue(hasWallet, "Should have WALLET notification");
        assertTrue(hasPayment, "Should have PAYMENT notification");
        assertTrue(hasAutoPay, "Should have AUTOPAY notification");
        assertTrue(hasInvestment, "Should have INVESTMENT notification");
        assertTrue(hasClaim, "Should have CLAIM notification");

        // Verify all notifications have correct user info
        allNotifications.forEach(notification -> {
            assertEquals(testUser.getId(), notification.getUserId());
            assertEquals(testPhoneNumber, notification.getPhoneNumber());
            assertEquals(testUsername, notification.getUsername());
            assertNotNull(notification.getTimestamp());
            assertNotNull(notification.getId());
        });

        // Mark some as read (e.g., read first 3)
        for (int i = 0; i < 3; i++) {
            notificationService.markAsRead(allNotifications.get(i).getId());
        }

        // Verify 2 unread remain
        assertEquals(2, notificationService.getUnreadCount(testPhoneNumber));
    }


    // Helper method to set private fields
    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set private field: " + fieldName, e);
        }
    }
}
