package com.example.mysterycard.entity;

import com.example.mysterycard.enums.StatusPayment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

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
    private String content;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private StatusPayment statusPayment = StatusPayment.PENDING;
    private String payType;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
     @OneToMany(mappedBy = "payment")
    private List<WalletTransaction> walletTransactions = new ArrayList<>();

}
