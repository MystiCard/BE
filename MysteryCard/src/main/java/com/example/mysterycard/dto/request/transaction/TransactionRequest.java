package com.example.mysterycard.dto.request.transaction;

import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class TransactionRequest {
    private UUID userId;
    @NotNull(message = "Transaction Type is required")
    private TransactionType transactionType;
    @NotNull(message = "Amount  is required")
    private Long amount;
    private PaymentRequest paymentRequest;
    private UUID bankId; // neu ma rut  tien
    private UUID orderId; // neu ma thanh toan cho order
    private UUID blindboxPurchaseId; // neu ma mua blindbox


    @Builder
    @Data
    public static class PaymentRequest {
        @NotNull
        @Size(message = "Provider is required. Please choose your provider", min = 3, max = 255)
        private String provider;
    }
}
