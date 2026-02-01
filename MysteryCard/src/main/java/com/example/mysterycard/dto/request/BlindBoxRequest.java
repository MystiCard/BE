package com.example.mysterycard.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BlindBoxRequest {
    private String name;
    private String description;
    private String imageUrl;
    private List<UUID> cardIds;
    private String categoryId;
}
