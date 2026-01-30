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
    @NotNull(message = "Buyer Id is required")
    private UUID buyerId;
    @NotNull(message = "Seller Id is required")
    private UUID sellerId;
    @NotNull(message = "Transaction Type is required")
    private TransactionType transactionType;
    @NotNull(message = "Amount  is required")
    private UUID orderId; // neu ma thanh toan cho order
    private UUID blindboxPurchaseId; // neu ma mua blindbox

}
