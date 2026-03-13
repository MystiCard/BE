package com.example.mysterycard.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponse {
    private CardResponse cardResponse;
    private SellResponse sellResponse;
    private int quantity;
    private double price;
}
