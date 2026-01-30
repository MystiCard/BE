package com.example.mysterycard.dto.response;

import com.example.mysterycard.enums.Rarity;
import lombok.Data;

import java.util.UUID;

@Data
public class RateConfigResponse {
    private UUID rateConfigId;
    private Rarity cardRarity;
    private double dropRate;
    private double variancePercent;

}
