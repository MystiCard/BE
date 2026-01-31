package com.example.mysterycard.dto.response;

import com.example.mysterycard.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrderResponse {
    private UUID orderId;
    private Long totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private int quantity;
}
