package com.example.mysterycard.dto.request.transaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class WithdrawRequest {
    @NotNull(message = "User id is required")
    private UUID userId;
    @NotNull(message = "Amount  is required")
    @Min(value = 1000,message = "Amount must be larger than 1000 VND")
    private Long amount;
    @NotNull(message = "Bank Id not null")
    private UUID bankId;

}
