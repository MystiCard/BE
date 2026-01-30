package com.example.mysterycard.dto.request;

import com.example.mysterycard.enums.Rarity;
import lombok.Data;

@Data
public class CardRequest {
    private String name;
    private Rarity rarity;
    private String imageUrl;
    private double basePrice;
    private String categoryId;
}
