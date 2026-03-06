package com.example.mysterycard.dto.response;

import com.example.mysterycard.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderBlindBoxResultResponse {
    private UUID orderId;
    private Long totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;
    List<BlindBoxResultResponse> blindBoxResults;
}
