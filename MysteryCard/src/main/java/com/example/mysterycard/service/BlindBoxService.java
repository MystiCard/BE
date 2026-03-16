package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.BlindBoxRequest;
import com.example.mysterycard.dto.response.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface BlindBoxService {
    BlindBoxResponse createBlindBox(BlindBoxRequest request);
    DrawResultResponse drawCard(UUID id);
    BlindBoxResponse getBlindBoxById(UUID id);
    List<BlindBoxResponse> getAllBlindBoxes();
    Page<BlindBoxCardResponse> getCardsInBlindBox(UUID blindBoxId, int size, int page);
    DrawResultResponse buyBlindBox(UUID blindBoxId);
    void deleteBlindBox(UUID id);
    BlindBoxProbabilitiesResponse getProbabilities(UUID blindBoxId);
    Page<BlindBoxResultResponse> getAllResultsForUser(int page, int size , UUID blindBoxId);
    Page<BlindBoxOpenResponse> getAllOpenedBlindBoxByUser(int page, int size);
}
