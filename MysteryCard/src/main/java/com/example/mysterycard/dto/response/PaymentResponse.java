package com.example.mysterycard.dto.response;

import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.enums.StatusPayment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public  class PaymentResponse {
    private UUID paymentId;
    private String provider;
    private Double amount;
    private String transactionRef;
    private StatusPayment statusPayment;
    private LocalDateTime createdAt;
    private String content;


}
