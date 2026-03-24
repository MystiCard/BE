package com.example.mysterycard.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class WishListResponse {
    private UUID wishListId;
     private CardResponse cardResponse;
    private Long expectPrice;
}
