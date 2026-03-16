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
@Builder
public class TransactionRequest {
    @NotNull(message = "Transaction Type is required")
    private TransactionType transactionType;
    private UUID orderId;
    private double drawPrice;
}
