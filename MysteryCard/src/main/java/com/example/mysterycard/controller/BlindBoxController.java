package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.BlindBoxRequest;
import com.example.mysterycard.dto.response.BlindBoxProbabilitiesResponse;
import com.example.mysterycard.service.BlindBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/blind-boxes")
public class BlindBoxController {
    @Autowired
    private BlindBoxService blindBoxService;

    @PostMapping
    public ApiResponse createBlindBox(@RequestBody BlindBoxRequest request) {
        return ApiResponse.success(blindBoxService.createBlindBox(request));
    }

    @GetMapping("/{id}/draw-card")
    public ApiResponse drawCard(@PathVariable("id") UUID id) {
        return ApiResponse.success(blindBoxService.drawCard(id));
    }

    @GetMapping
    public ApiResponse getAllBlindBoxes() {
        return ApiResponse.success(blindBoxService.getAllBlindBoxes());
    }

    @GetMapping("/{id}")
    public ApiResponse getBlindBoxById(@PathVariable("id") UUID id) {
        return ApiResponse.success(blindBoxService.getBlindBoxById(id));
    }

    @GetMapping("/{id}/cards")
    public ApiResponse getCardsInBlindBox(
            @RequestParam(required = false, defaultValue = "0 ") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @PathVariable("id") UUID blindBoxId) {
        return ApiResponse.success(blindBoxService.getCardsInBlindBox(blindBoxId,size,page));
    }
    @PostMapping("/{id}/buy")
    public ApiResponse buyBlindBox(@PathVariable("id") UUID blindBoxId) {
        return ApiResponse.success(blindBoxService.buyBlindBox(blindBoxId));
    }
    @DeleteMapping("/{id}")
    public ApiResponse deleteBlindBox(@PathVariable("id") UUID id) {
        blindBoxService.deleteBlindBox(id);
        return ApiResponse.success();
    }
    @GetMapping("/{id}/probabilities")
    public BlindBoxProbabilitiesResponse getProbabilities(@PathVariable UUID id) {
        return blindBoxService.getProbabilities(id);
    }

    @GetMapping("/results/{blindBoxId}")
    public ApiResponse getAllResultsForUser(
            @RequestParam(required = false, defaultValue = "0 ") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @PathVariable UUID blindBoxId
    ) {
        return ApiResponse.success(blindBoxService.getAllResultsForUser(page, size ,blindBoxId));
    }
    @GetMapping("/results")
    public ApiResponse getAllOpenedBlindBoxForUser(
            @RequestParam(required = false, defaultValue = "0 ") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ApiResponse.success(blindBoxService.getAllOpenedBlindBoxByUser(page, size ));
    }

}
