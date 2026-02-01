package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "BlindBoxResult")
@NoArgsConstructor
@Getter
@Setter
public class BlindBoxResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID blindBoxResultId;
    @CreationTimestamp
    private LocalDateTime openedAt;
    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users owner;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

}
