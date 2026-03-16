package com.example.mysterycard.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ChangeAddressShipmentRequest {
   // private  UUID ListsellerId;
    private UUID orderId;
    private Long toDistrictId;
    private Long toWardId;
    private String newAddress;
    private String toName;
    private String  toPhone;
//    private Long oldShipmentFee;
//    private Long totalPrice;
}
