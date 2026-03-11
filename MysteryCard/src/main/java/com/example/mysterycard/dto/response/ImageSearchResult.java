package com.example.mysterycard.dto.response;

import java.util.UUID;

public interface ImageSearchResult {
    UUID getImageId();
    UUID getCardId();
    String getImageUrl();
}
