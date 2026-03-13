package com.example.mysterycard.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CartResponse {
    private UUID cartId;
    private CardResponse cardResponse;
    private SellResponse sellResponse;
    private int quantity;
    private double price;
}
