package com.example.mysterycard.dto.response;

import com.example.mysterycard.entity.CardRequired.RequiredStatus;
import com.example.mysterycard.enums.Rarity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CardRequiredResponse {
    private UUID cardRequiredId;
    private String cardName;
    private Rarity rate;
    private double basePrice;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime decidedAt;
    private String note;
    private RequiredStatus status = RequiredStatus.PENDING;
    private String userName;
    private String categoryName;

}
