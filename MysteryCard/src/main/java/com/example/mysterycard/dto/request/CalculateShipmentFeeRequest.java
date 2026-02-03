package com.example.mysterycard.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalculateShipmentFeeRequest {

    private Long serviceId;

    private Long insuranceValue;

    private String coupon;

    private Long fromDistrictId;

    private Long toDistrictId;

    private Long toWardCode;

    private Integer height;

    private Integer length;

    private Integer width;

    private Integer weight;
}
