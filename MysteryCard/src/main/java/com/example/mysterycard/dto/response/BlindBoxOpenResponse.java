package com.example.mysterycard.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BlindBoxOpenResponse {
    private UUID blindBoxId;
    private String name;
    private String imageUrl;
}
