package com.example.mysterycard.dto.response;

import com.example.mysterycard.enums.ShippingStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ShipmentResponse {
    private UUID shipmentId;
    private String toAddress;
    private ShippingStatus shipmentStatus;
    private Long shipmentFee;
    private String toPhone;
    private String fromPhone;
    private String fromAddress;
    private String fromName;
    private String toName;
    private Long toDistrictId;
    private Long toWardId;
    private LocalDateTime createAt;
    List<TrackingResponse> trackingResponses;

}
