package com.example.mysterycard.dto.response;

import com.example.mysterycard.enums.Rarity;
import lombok.Data;

@Data
public class CardResponse {
    private String name;
    private Rarity rarity;
    private String imageUrl;
    private String categoryName;
}
