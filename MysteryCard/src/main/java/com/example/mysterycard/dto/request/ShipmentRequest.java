package com.example.mysterycard.dto.request;

import com.example.mysterycard.entity.Order;
import com.example.mysterycard.enums.ShippingStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.UUID;

@Data
public class ShipmentRequest {
    private UUID orderId;
    private String buyerAddress;
    private Long fromDistrictId;
    private Long toDistrictId;
    private Long toWardId;
    private String buyerPhone;
    private String sellerPhone;
    private String sellerAddress;
}
