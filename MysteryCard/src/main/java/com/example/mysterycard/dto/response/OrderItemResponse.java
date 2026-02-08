package com.example.mysterycard.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderItemResponse {
    private double shipfee;
    List<OrderDetailResponse> orderDetailResponseList;
    @Builder
    @Data
    public static class OrderDetailResponse {
        private UUID orderItemId;
        private int quantity;
        private double price;


    }}
