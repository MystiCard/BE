package com.example.mysterycard.dto.response;

import com.example.mysterycard.enums.OrderItemStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderItemResponse {
    private double shipfee;
    private ShipmentResponse shipmentResponse;
    List<OrderDetailResponse> orderDetailResponseList;
    @Builder
    @Data
    public static class OrderDetailResponse {
        private UUID orderItemId;
        private int quantity;
        private double price;
        private OrderItemStatus orderItemStatus;

    }}
