package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.response.NotificationResponse;
import com.example.mysterycard.entity.Card;
import com.example.mysterycard.entity.Notification;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.entity.WishList;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.NotificationMapper;
import com.example.mysterycard.repository.NotificationRepo;
import com.example.mysterycard.repository.UsersRepo;
import com.example.mysterycard.service.NotificationService;
import com.example.mysterycard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final NotificationMapper notificationMapper;
    private final UsersRepo usersRepo;

    @Override
    public NotificationResponse getNotificationById(UUID notificationId) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        return notificationMapper.toResponse(notification);
    }

    @Override
    public NotificationResponse createNotification(Card card, String message , Users user , Notification.NotiType notiType) {
        Notification notification = new Notification();
        notification.setCard(card);
        notification.setMessage(message);
        notification.setUsers(user);
        notification.setNotiType(notiType);
        return notificationMapper.toResponse(notificationRepo.save(notification));

    }

    @Override
    public NotificationResponse createNotification(String message, Users user, Notification.NotiType notiType) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUsers(user);
        notification.setNotiType(notiType);
        return notificationMapper.toResponse(notificationRepo.save(notification));
    }

    @Override
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.setRead(true);
        notificationRepo.save(notification);
    }

    @Override
    public void deleteNotification(UUID notificationId) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.setDeleted(true);
        notificationRepo.save(notification);
    }

    @Override
    public Page<NotificationResponse> getNotificationsForUser( int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (email == null) {
            throw new AppException(ErrorCode.INVALID_AUTHENCATION);
        }
        Users users = usersRepo.findByEmail(email);
        if(users == null)
        {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return notificationRepo.findByUsersUserId(users.getUserId(), pageable)
                .map(notificationMapper::toResponse);
    }
}
