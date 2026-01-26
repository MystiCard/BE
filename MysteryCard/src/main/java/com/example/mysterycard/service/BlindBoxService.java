package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.BlindBoxRequest;
import com.example.mysterycard.dto.response.BlindBoxCardResponse;
import com.example.mysterycard.dto.response.BlindBoxResponse;
import com.example.mysterycard.dto.response.CardResponse;

import java.util.List;
import java.util.UUID;

public interface BlindBoxService {
    BlindBoxResponse createBlindBox(BlindBoxRequest request);
    CardResponse drawCard(Long id);
    BlindBoxResponse getBlindBoxById(Long id);
    List<BlindBoxResponse> getAllBlindBoxes();
    List<BlindBoxCardResponse> getCardsInBlindBox(Long blindBoxId);
}
