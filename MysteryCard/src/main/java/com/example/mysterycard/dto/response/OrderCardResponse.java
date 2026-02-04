package com.example.mysterycard.dto.response;

import com.example.mysterycard.entity.OrderItem;
import com.example.mysterycard.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Data
public class OrderCardResponse {
    private UUID orderId;
    private Long totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private int quantity;
    private double shipfee;
    List<OrderItemResponse> orderItems;

}
