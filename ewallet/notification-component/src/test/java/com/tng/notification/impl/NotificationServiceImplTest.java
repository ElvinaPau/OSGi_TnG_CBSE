package com.tng.notification.impl;

import com.tng.NotificationData;
import com.tng.NotificationService;
import com.tng.User;
import com.tng.UserService;
import com.tng.WalletService;
import com.tng.PaymentService;
import com.tng.InvestmentService;
import com.tng.InsuranceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("NotificationServiceImpl Tests")
class NotificationServiceImplTest {

    private NotificationServiceImpl notificationService;

    @Mock
    private UserService userService;

    @Mock
    private WalletService walletService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private InvestmentService investmentService;

    @Mock
    private InsuranceService insuranceService;

    private User testUser;
    private String testPhoneNumber = "0123456789";
    private String testUsername = "TestUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationServiceImpl();

        // Inject mocked dependencies using reflection since they're private fields
        setPrivateField(notificationService, "userService", userService);
        setPrivateField(notificationService, "walletService", walletService);
        setPrivateField(notificationService, "paymentService", paymentService);
        setPrivateField(notificationService, "investmentService", investmentService);
        setPrivateField(notificationService, "insuranceService", insuranceService);

        // Create test user
        testUser = new User(testUsername, testPhoneNumber);

        // Mock UserService to return test user
        when(userService.getUser(testPhoneNumber)).thenReturn(testUser);
    }

    @Test
    @DisplayName("Should generate notification successfully")
    void testGenerateNotification() {
        // Arrange
        String type = "WALLET";
        String message = "RM 50 added to your wallet";

        // Act
        notificationService.generateNotification(testPhoneNumber, type, message);

        // Assert
        List<NotificationData> notifications = notificationService.getAllNotifications(testPhoneNumber);
        assertNotNull(notifications);
        assertEquals(1, notifications.size());

        NotificationData notification = notifications.get(0);
        assertEquals(type, notification.getType());
        assertEquals(message, notification.getMessage());
        assertEquals(testPhoneNumber, notification.getPhoneNumber());
        assertEquals(testUser.getId(), notification.getUserId());
        assertEquals(testUsername, notification.getUsername());
        assertFalse(notification.isRead());
    }


    @Test
    @DisplayName("Should retrieve only unread notifications")
    void testGetUnreadNotifications() {
        // Arrange
        notificationService.generateNotification(testPhoneNumber, "WALLET", "RM 50 added");
        notificationService.generateNotification(testPhoneNumber, "PAYMENT", "Payment processed");
        notificationService.generateNotification(testPhoneNumber, "INVESTMENT", "Investment successful");

        // Mark first notification as read
        List<NotificationData> allNotifications = notificationService.getAllNotifications(testPhoneNumber);
        notificationService.markAsRead(allNotifications.get(0).getId());

        // Act
        List<NotificationData> unreadNotifications = notificationService.getUnreadNotifications(testPhoneNumber);

        // Assert
        assertEquals(2, unreadNotifications.size());
        assertTrue(unreadNotifications.stream().allMatch(n -> !n.isRead()));
    }


    @Test
    @DisplayName("Should get correct unread notification count")
    void testGetUnreadCount() {
        // Arrange
        notificationService.generateNotification(testPhoneNumber, "WALLET", "RM 50 added");
        notificationService.generateNotification(testPhoneNumber, "PAYMENT", "Payment processed");
        notificationService.generateNotification(testPhoneNumber, "INVESTMENT", "Investment successful");

        List<NotificationData> allNotifications = notificationService.getAllNotifications(testPhoneNumber);
        notificationService.markAsRead(allNotifications.get(0).getId());

        // Act
        int unreadCount = notificationService.getUnreadCount(testPhoneNumber);

        // Assert
        assertEquals(2, unreadCount);
    }


    @Test
    @DisplayName("Should mark all notifications as read for a phone number")
    void testMarkAllAsRead() {
        // Arrange
        notificationService.generateNotification(testPhoneNumber, "WALLET", "RM 50 added");
        notificationService.generateNotification(testPhoneNumber, "PAYMENT", "Payment processed");
        notificationService.generateNotification(testPhoneNumber, "INVESTMENT", "Investment successful");

        // Act
        notificationService.markAllAsRead(testPhoneNumber);

        // Assert
        List<NotificationData> allNotifications = notificationService.getAllNotifications(testPhoneNumber);
        assertTrue(allNotifications.stream().allMatch(NotificationData::isRead));
    }

   

    @Test
    @DisplayName("Should handle display notifications without errors")
    void testDisplayNotifications() {
        // Arrange
        notificationService.generateNotification(testPhoneNumber, "WALLET", "RM 50 added");
        notificationService.generateNotification(testPhoneNumber, "PAYMENT", "Payment processed");

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> 
            notificationService.displayNotifications(testPhoneNumber)
        );
    }

    // Helper method to set private fields for testing
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
