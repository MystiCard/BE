package com.example.mysterycard.dto.request;

import com.example.mysterycard.enums.ShippingStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ShipmentRequest {
    private List<UUID> orderItemId;
    private String toAddress;
    private Long toDistrictId;
    private Long toWardId;
    private String toPhone;
    private Long shipmentFee;
    private String fromPhone;
    private String fromAddress;
    private Long fromDistrictId;
}