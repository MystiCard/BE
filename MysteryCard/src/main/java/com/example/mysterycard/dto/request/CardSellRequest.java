package com.example.mysterycard.dto.request;

import com.example.mysterycard.enums.Rarity;
import lombok.Data;

@Data
public class CardSellRequest {
    private String keyword;
    private Rarity rarity;
    private Double min;
    private Double max;
    private String sort;
}
