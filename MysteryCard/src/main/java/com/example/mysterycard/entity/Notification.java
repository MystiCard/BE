package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID notificationId;
    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;
    private String message;
    private boolean isRead = false;
    private boolean isDeleted = false;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private NotiType notiType;

    public enum NotiType {
        wishList,
        shipment,
        wallet,
        addCard
    }
}
