package com.example.mysterycard.dto.response.transaction;

import com.example.mysterycard.dto.response.bankAccount.BankAccountResponse;
import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionResponse {
    private UUID walletTransactionId;
    private TransactionType transactionType;
    private double amount;
    private StatusPayment statusTransaction;
    private LocalDateTime createAt;
    private PaymentResponse paymentResponse;
    private String message;
    private BankAccountResponse bankAccountResponse;

    @Builder
    @Data
    public static class PaymentResponse {
        private UUID paymentId;
        private String provider;
        private Double amount;
        private String transactionRef;
        private StatusPayment status;
        private LocalDateTime createdAt;
        private String message;


    }
}
