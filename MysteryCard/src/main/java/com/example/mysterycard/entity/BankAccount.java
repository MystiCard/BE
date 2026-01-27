package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "BankAccount")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bankAccountId;
    private String bankCode;
    private String accountNumber;
    private String accountName;
    private boolean defaultAccount;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
    @OneToMany(mappedBy = "bankAccount")
    private List<WalletTransaction> walletTransaction = new ArrayList<>();


}
