package com.example.mysterycard.entity;

import com.example.mysterycard.enums.StatusPayment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Payment")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;
    private String provider;
    private String transactionRef;
    private Long amount;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
     @OneToOne(mappedBy = "payment")
    private WalletTransaction walletTransaction;

}
