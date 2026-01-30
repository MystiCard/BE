package com.example.mysterycard.dto.request;

import com.example.mysterycard.enums.Rarity;
import lombok.Data;

@Data
public class RateConfigRequest {
    private Rarity cardRarity;
    private double dropRate;
    private double variancePercent;
}
