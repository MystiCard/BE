package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.BlindBoxRequest;
import com.example.mysterycard.dto.response.*;

import java.util.List;
import java.util.UUID;

public interface BlindBoxService {
    BlindBoxResponse createBlindBox(BlindBoxRequest request);
    DrawResultResponse drawCard(UUID id);
    BlindBoxResponse getBlindBoxById(UUID id);
    List<BlindBoxResponse> getAllBlindBoxes();
    List<BlindBoxCardResponse> getCardsInBlindBox(UUID blindBoxId);
    OrderResponse buyBlindBox(UUID blindBoxId);
    void deleteBlindBox(UUID id);
    BlindBoxProbabilitiesResponse getProbabilities(UUID blindBoxId);
}
