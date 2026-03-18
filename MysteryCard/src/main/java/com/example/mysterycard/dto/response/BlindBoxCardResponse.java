package com.example.mysterycard.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class BlindBoxCardResponse {
    private UUID blindBoxCardId;
    private UUID blindBoxId;
    private CardResponse cardResponse;
    private boolean status;
    private double rate;
}
