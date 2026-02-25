package com.example.mysterycard.dto.request;

import com.example.mysterycard.entity.Order;
import com.example.mysterycard.enums.ShippingStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ShipmentRequest {
    private List<UUID> orderItemId;
    private String buyerAddress;
    private Long toDistrictId;
    private Long toWardId;
    private String buyerPhone;
    private Long shipmentFee;
}
