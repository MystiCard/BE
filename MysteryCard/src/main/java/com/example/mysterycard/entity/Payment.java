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
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;
    private String provider;
    private Double amount;
    private String transaction_ref;
    @Enumerated(EnumType.STRING)
    private StatusPayment status;
    private LocalDateTime createdAt;
     @OneToOne(mappedBy = "payment")
    private WalletTransaction walletTransaction;

}
