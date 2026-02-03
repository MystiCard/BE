package com.example.mysterycard.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class AsignShipperRequest {
    private UUID shipperId;
    private UUID shipmentId;
}
