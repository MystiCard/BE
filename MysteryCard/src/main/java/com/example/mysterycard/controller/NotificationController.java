package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.response.NotificationResponse;
import com.example.mysterycard.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationById(@PathVariable UUID notificationId) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getNotificationById(notificationId)));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<?>> getNotificationsForUser(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getNotificationsForUser(page, size)));
    }

    @PutMapping("/mark-as-read/{notificationId}")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read successfully"));
    }

    @PutMapping("/delete/{notificationId}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(@PathVariable UUID notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully"));
    }
}
