package com.example.mysterycard.dto.request.transaction;

import com.example.mysterycard.enums.StatusPayment;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpdateTransactionStatusRequest {
    private StatusPayment statusPayment;
    private UUID tranferId;
    private String message;
}
