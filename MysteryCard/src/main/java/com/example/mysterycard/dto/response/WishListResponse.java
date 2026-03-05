package com.example.mysterycard.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class WishListResponse {
    private UUID wishListId;
    private String userId;
    private String cardId;
    private Long expectPrice;
}
