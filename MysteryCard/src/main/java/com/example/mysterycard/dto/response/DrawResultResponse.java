package com.example.mysterycard.dto.response;

import lombok.Data;

@Data
public class DrawResultResponse {
    private CardResponse card;
    private double drawPrice; // Giá mở box sau khi rút
    private double profitOrLoss; // Lời/lỗ so với giá box
}
