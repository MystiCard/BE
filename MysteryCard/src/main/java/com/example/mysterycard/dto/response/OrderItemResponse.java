package com.example.mysterycard.dto.response;

import com.example.mysterycard.enums.OrderItemStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemResponse {
    private double shipfee;
    private ShipmentResponse shipmentResponse;
    private List<OrderDetailResponse> orderDetailResponseList;
    /**
     * Danh sách thẻ nếu shipment là từ Hộp bí ẩn (không có OrderItem).
     */
    private List<BlindBoxShipmentItemResponse> blindBoxDetails;

    @Builder
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OrderDetailResponse {
        private UUID orderItemId;
        private int quantity;
        private double price;
        private CardResponse cardResponse;
        private OrderItemStatus orderItemStatus;
    }
}

