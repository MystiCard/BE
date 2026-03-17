package com.example.mysterycard.dto.response;

import com.example.mysterycard.enums.BlindBoxStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.UUID;

@Data
public class BlindBoxResponse {
    private UUID blindBoxId;
    private String name;
    private String description;
    private String imageUrl;
    private double drawPrice;
    private Long allBoxPrice;
    private BlindBoxStatus blindBoxStatus ;
}
