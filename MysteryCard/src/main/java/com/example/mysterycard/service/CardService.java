package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.CardRequest;
import com.example.mysterycard.dto.response.CardResponse;

import java.util.List;
import java.util.UUID;

public interface CardService {
    CardResponse getCardById(UUID id);
    List<CardResponse> getAllCards();
    void deleteCardById(UUID id);
    CardResponse createCard(CardRequest request);
    CardResponse updateCard(UUID id, CardRequest request);
}
