package com.example.mysterycard.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalculateShipmentFeeRequest {

    private Long service_id;

    private Long insurance_value;

    private String coupon;

    private Long from_district_id;

    private Long to_district_id;

    private String to_ward_code;

    private Integer height;

    private Integer length;

    private Integer width;

    private Integer weight;
}
