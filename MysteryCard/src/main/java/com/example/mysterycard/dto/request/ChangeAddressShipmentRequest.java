package com.example.mysterycard.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ChangeAddressShipmentRequest {
    private UUID shipmentId;
    private Long feeShipmentId;
    private String buyerAddress;
    private Long fromDistrictId;
    private Long toDistrictId;
    private Long toWardId;
    private String sellerAddress;
}
