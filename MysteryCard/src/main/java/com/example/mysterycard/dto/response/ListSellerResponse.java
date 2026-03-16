package com.example.mysterycard.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class ListSellerResponse {
    private UUID listSellerId;
    private double price;
    private int quantity;
    private CardResponse cardResponse;
    private UserResponse sellerResponse;

}
