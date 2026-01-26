package com.example.mysterycard.dto.response;

import lombok.Data;

@Data
public class BlindBoxResponse {
    private Long blindBoxId;
    private String name;
    private String description;
    private String imageUrl;
    private double price;
}
