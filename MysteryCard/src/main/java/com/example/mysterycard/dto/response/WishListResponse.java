package com.example.mysterycard.dto.response;

import lombok.Data;

@Data
public class WishListResponse {
    private String wishListId;
    private String userId;
    private String cardId;
    private Long expectPrice;
}
