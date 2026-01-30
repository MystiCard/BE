package com.example.mysterycard.dto.request.transaction;

import com.example.mysterycard.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProcessTransactionRequest {
    private UUID transactionId;
    private OrderStatus orderStatus;
    private Long amount;
    private Long shipmentFee;
}
