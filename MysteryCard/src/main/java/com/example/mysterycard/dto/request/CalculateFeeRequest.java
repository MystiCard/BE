package com.example.mysterycard.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CalculateFeeRequest {
    private double totalAmount;
    private Long fromDistrictId;
    private Long toDistrictId;
    private String toWardId;
}
