package com.example.mysterycard.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class BlindBoxResponse {
    private UUID blindBoxId;
    private String name;
    private String description;
    private String imageUrl;
    private double drawPrice;
}
