package com.example.mysterycard.dto.request;

import com.example.mysterycard.enums.Rarity;
import lombok.Data;

import java.util.UUID;

@Data
public class NewCardRequest {
    private String cardName;
    private Rarity rate;
    private double basePrice;
    private String category;
    private UUID categoryId;
}
