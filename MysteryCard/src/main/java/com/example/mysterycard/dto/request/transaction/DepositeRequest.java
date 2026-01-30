package com.example.mysterycard.dto.request.transaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class DepositeRequest {
    @NotNull(message = "User id is required")
    private UUID userId;
    @NotNull(message = "Amount  is required")
    private Long amount;
    @NotNull
    @Size(message = "Provider is required. Please choose your provider", min = 3, max = 255)
    private String provider;
}
