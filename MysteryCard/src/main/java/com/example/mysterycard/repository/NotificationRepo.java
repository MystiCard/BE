package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepo extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUsersUserId(UUID userId, Pageable pageable);
}
