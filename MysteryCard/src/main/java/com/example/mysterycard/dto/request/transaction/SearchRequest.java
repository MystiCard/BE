package com.example.mysterycard.dto.request.transaction;

import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.enums.TransactionType;
import lombok.Data;

import java.util.UUID;

@Data
public class SearchRequest {
    private StatusPayment statusPayment;
    private TransactionType transactionType;
}
