package com.example.mysterycard.dto.request.bankAccount;

import lombok.Data;

import java.util.UUID;

@Data
public class ChangeDefaultRequest {
    private UUID bankId;
    private UUID userId;
}
