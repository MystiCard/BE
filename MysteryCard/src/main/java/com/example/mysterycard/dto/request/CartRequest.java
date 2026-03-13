package com.example.mysterycard.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CartRequest {
    private int quantity;
    private UUID listSellerId;
}
