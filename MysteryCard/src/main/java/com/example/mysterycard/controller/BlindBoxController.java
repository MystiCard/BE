package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.BlindBoxRequest;
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
    public ApiResponse drawCard(@PathVariable("id") Long id) {
        return ApiResponse.success(blindBoxService.drawCard(id));
    }

    @GetMapping
    public ApiResponse getAllBlindBoxes() {
        return ApiResponse.success(blindBoxService.getAllBlindBoxes());
    }

    @GetMapping("/{id}")
    public ApiResponse getBlindBoxById(@PathVariable("id") Long id) {
        return ApiResponse.success(blindBoxService.getBlindBoxById(id));
    }

    @GetMapping("/{id}/cards")
    public ApiResponse getCardsInBlindBox(@PathVariable("id") Long blindBoxId) {
        return ApiResponse.success(blindBoxService.getCardsInBlindBox(blindBoxId));
    }

}
