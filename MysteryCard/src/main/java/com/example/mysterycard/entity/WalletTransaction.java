package com.example.mysterycard.entity;

import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "WalletTransaction")
@NoArgsConstructor
@Getter
@Setter
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID walletTransactionId;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private double amount;
    @Enumerated(EnumType.STRING)
    private StatusPayment statusTransaction;
    private LocalDateTime createAt;
    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;
    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "wallet_send_id")
    private Wallet walletSend;
    @ManyToOne
    @JoinColumn(name = "wallet_receive_id")
    private Wallet walletReceive;
    @ManyToOne
    @JoinColumn(name = "blindboxpurchase_id")
    private BlindBoxPurChase blindboxpurchase;
}
