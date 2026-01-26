package com.example.mysterycard.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class BlindBoxCardResponse {
    private UUID blindBoxCardId;
    private Long blindBoxId;
    private UUID cardId;
    private String cardName;
    private String rarity;
    private boolean status;
    private double rate;
}
