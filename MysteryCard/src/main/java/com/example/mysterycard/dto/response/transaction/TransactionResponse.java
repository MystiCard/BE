package com.example.mysterycard.dto.response.transaction;

import com.example.mysterycard.dto.response.bankAccount.BankAccountResponse;
import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import org.hibernate.query.derived.AnonymousTupleBasicEntityIdentifierMapping;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {
    private UUID walletTransactionId;
    private TransactionType transactionType;
    private double amount;
    private StatusPayment statusTransaction;
    private LocalDateTime createAt;
    private String message;
    private BankAccountResponse bankAccountResponse;


}
