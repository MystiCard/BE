package com.example.mysterycard.dto.response;

import com.example.mysterycard.entity.Notification;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationResponse {
    private UUID notificationId;
    private UUID cardId;
    private UUID userId;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private Notification.NotiType notiType;
}
