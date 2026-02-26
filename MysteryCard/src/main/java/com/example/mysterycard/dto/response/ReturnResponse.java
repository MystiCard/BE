package com.example.mysterycard.dto.response;

import com.example.mysterycard.entity.Image;
import com.example.mysterycard.enums.ReturnRequestStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public class ReturnResponse {
    private UUID returnRequestId;
    private String reason;
    private ReturnRequestStatus status ;
    private List<OrderItemResponse.OrderDetailResponse> orderItems;
    private List<ImageResponse> listImages;
    private LocalDateTime createdAt;
    private ShipmentResponse shipmentResponse;
}
