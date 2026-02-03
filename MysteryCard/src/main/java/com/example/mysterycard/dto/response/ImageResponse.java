package com.example.mysterycard.dto.response;

import lombok.Data;

import java.util.UUID;
@Data
public class ImageResponse {
    private UUID imageId;
    private String imageUrl;
}
