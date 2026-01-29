package com.example.mysterycard.dto.response;

import com.example.mysterycard.entity.BlindBox;
import com.example.mysterycard.entity.Users;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BlindBoxPurChaseResponse {
    private UUID blindBoxPurchaseId;
    private double price;
    private LocalDateTime purchaseDate;
    private String buyer;
    private String blindBoxName;


}
