package com.example.mysterycard.dto.request.transaction;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ApproveRequest {
    @NotNull(message = "Transaction id is required")
    private UUID transactionId;
    @NotNull(message = "Provider id is required")
    private String provider;
}
