package com.example.mysterycard.dto.request;

import com.example.mysterycard.enums.ShippingStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpdateShipmentRequest {
    private UUID shipmentId;
    private ShippingStatus shippingStatus;
    private String note;

}
