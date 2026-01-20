package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Builder
@Entity
@Table(name = "RefreshToken")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long refreshTokenId;
    private String token;
    private LocalDateTime expiryDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
