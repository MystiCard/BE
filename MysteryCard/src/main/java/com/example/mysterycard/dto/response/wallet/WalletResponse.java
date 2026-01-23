package com.example.mysterycard.dto.response.wallet;

import com.example.mysterycard.enums.WalletStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.UUID;

@Data
public class WalletResponse {
    private UUID walletId;
    private Double balance;
    private WalletStatus walletStatus;
}
