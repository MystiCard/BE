package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.BlindBoxRequest;
import com.example.mysterycard.dto.response.*;
import com.example.mysterycard.entity.BlindBoxResult;
import com.example.mysterycard.enums.BlindBoxStatus;
import com.example.mysterycard.enums.ShippingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface BlindBoxService {
    BlindBoxResponse createBlindBox(BlindBoxRequest request, MultipartFile file);
    DrawResultResponse drawCard(UUID id);
    BlindBoxResponse getBlindBoxById(UUID id);
    Page<BlindBoxResponse> getAllBlindBoxes(int page, int size, BlindBoxStatus blindBoxStatus);
    Page<BlindBoxCardResponse> getCardsInBlindBox(UUID blindBoxId, int size, int page);
    List<DrawResultResponse> buyBlindBox(UUID blindBoxId, boolean buyAll);
    void deleteBlindBox(UUID id);
    BlindBoxProbabilitiesResponse getProbabilities(UUID blindBoxId);
    Page<BlindBoxResultResponse> getAllResultsForUser(int page, int size , UUID blindBoxId, BlindBoxResult.ResultStatus status);
    Page<BlindBoxOpenResponse> getAllOpenedBlindBoxByUser(int page, int size);
    BlindBoxResultResponse toBlindBoxResultResponse(BlindBoxResult result);
    Page<BlindBoxResultResponse> getALlResultsOpened(int page, int size, BlindBoxResult.ResultStatus resultStatus);
    PageResponse<BlindBoxShipmentResponse> getBlindBoxShipment(int page, int size, ShippingStatus status);
}
