package com.example.mysterycard.dto.response;

import com.example.mysterycard.enums.Status;
import lombok.Data;

import java.util.UUID;

@Data
public class SellResponse {
    private UUID listSellerId;
    private double price;
    private int quantity;
    private Status status;
    private UUID cardId;
    private UUID sellerId;

}
