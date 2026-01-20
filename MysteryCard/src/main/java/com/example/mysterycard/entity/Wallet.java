package com.example.mysterycard.entity;

import com.example.mysterycard.enums.WalletStatus;
import jakarta.persistence.*;
import jakarta.transaction.Transaction;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Wallet")
@NoArgsConstructor
@Getter
@Setter
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID walletId;
    private Double balance;
    @Enumerated(EnumType.STRING)
    private WalletStatus walletStatus;
    @OneToMany(mappedBy = "walletSend")
    private Set<WalletTransaction> sentTransactions = new HashSet<>();
    @OneToMany(mappedBy = "walletReceive")
    private Set<WalletTransaction> receivedTransactions = new HashSet<>();
}
