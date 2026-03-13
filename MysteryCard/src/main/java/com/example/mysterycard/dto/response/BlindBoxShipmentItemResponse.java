package com.example.mysterycard.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlindBoxShipmentItemResponse {
    private String cardName;
    private String cardImageUrl;
    private Long basePrice;
}

