package com.example.mysterycard.service;

import com.example.mysterycard.dto.response.NotificationResponse;
import com.example.mysterycard.entity.Card;
import com.example.mysterycard.entity.Notification;
import com.example.mysterycard.entity.Users;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface NotificationService {
    NotificationResponse getNotificationById(UUID notificationId);
    NotificationResponse createNotification(Card card, String message, Users user, Notification.NotiType notiType);
    NotificationResponse createNotification(String message, Users user, Notification.NotiType notiType);
    void markAsRead(UUID notificationId);
    void deleteNotification(UUID notificationId);
    Page<NotificationResponse> getNotificationsForUser( int page, int size);
}
