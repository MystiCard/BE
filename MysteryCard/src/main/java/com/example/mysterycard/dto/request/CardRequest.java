package com.example.mysterycard.dto.request;

import com.example.mysterycard.enums.Rarity;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CardRequest {
    private String name;
    private Rarity rarity;
    private double basePrice;
    private UUID categoryId;
}
