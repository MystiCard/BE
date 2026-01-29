package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.BlindBoxRequest;
import com.example.mysterycard.dto.response.*;

import java.util.List;
import java.util.UUID;

public interface BlindBoxService {
    BlindBoxResponse createBlindBox(BlindBoxRequest request);
    CardResponse drawCard(UUID id);
    BlindBoxResponse getBlindBoxById(Long id);
    List<BlindBoxResponse> getAllBlindBoxes();
    List<BlindBoxCardResponse> getCardsInBlindBox(Long blindBoxId);
    BlindBoxPurChaseResponse buyBlindBox(Long blindBoxId);
    void deleteBlindBox(Long id);
    BlindBoxProbabilitiesResponse getProbabilities(Long blindBoxId);
}
