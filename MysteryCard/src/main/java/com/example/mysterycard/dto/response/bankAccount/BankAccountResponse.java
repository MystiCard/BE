package com.example.mysterycard.dto.response.bankAccount;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BankAccountResponse {
    private UUID bankAccountId;
    private String bankCode;
    private String accountNumber;
    private String accountName;
    private boolean defaultAccount;
}
