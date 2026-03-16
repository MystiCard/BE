package com.example.mysterycard.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BlindBoxResultResponse {
    private UUID blindBoxResultId;
    private LocalDateTime openedAt;
    private String cardName;
    private String cardImageUrl;
    private String rarity;
    private String blindBoxName;

}
