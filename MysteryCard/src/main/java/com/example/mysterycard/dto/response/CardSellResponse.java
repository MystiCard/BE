package com.example.mysterycard.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardSellResponse {
    private CardResponse cardResponse;
    private int numberOfCard;
    private int numberOfSeller;
}
