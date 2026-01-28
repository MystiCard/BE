package com.example.mysterycard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "EmailVerify")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EmailVerify {
    @Id
    private String email;
    private LocalDateTime expiryDate;
    private String code;
    private int wrongNumber;
}
