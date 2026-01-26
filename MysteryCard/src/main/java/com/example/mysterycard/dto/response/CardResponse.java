package com.example.mysterycard.dto.response;

import com.example.mysterycard.enums.Rarity;
import lombok.Data;

import java.util.UUID;

@Data
public class CardResponse {
    private UUID cardId;
    private String name;
    private Rarity rarity;
    private String imageUrl;
    private String categoryName;
}
