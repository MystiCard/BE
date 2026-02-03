package com.example.mysterycard.dto.response;

import com.example.mysterycard.enums.ShippingStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ShipmentResponse {
    private UUID shipmentId;
    private String buyerAddress;
    private ShippingStatus shipmentStatus;
    private Long shipmentFee;
    private String buyerPhone;
    private String sellerPhone;
    private String sellerAddress;
    private LocalDateTime createAt;
}
