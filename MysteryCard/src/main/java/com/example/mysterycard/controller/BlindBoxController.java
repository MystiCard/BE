package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.BlindBoxRequest;
import com.example.mysterycard.dto.response.BlindBoxProbabilitiesResponse;
import com.example.mysterycard.entity.BlindBoxResult;
import com.example.mysterycard.enums.BlindBoxStatus;
import com.example.mysterycard.enums.ShippingStatus;
import com.example.mysterycard.service.BlindBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@RestController
@RequestMapping("/api/blind-boxes")
public class BlindBoxController {
    @Autowired
    private BlindBoxService blindBoxService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse createBlindBox(
            @RequestPart BlindBoxRequest request
            ,@RequestPart MultipartFile file ) {
        return ApiResponse.success(blindBoxService.createBlindBox(request,file));
    }

    @GetMapping("/{id}/draw-card")
    public ApiResponse drawCard(@PathVariable("id") UUID id) {
        return ApiResponse.success(blindBoxService.drawCard(id));
    }

    @GetMapping
    public ApiResponse getAllBlindBoxes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam (required = false)BlindBoxStatus blindBoxStatus
            ) {
        return ApiResponse.success(blindBoxService.getAllBlindBoxes(page,size,blindBoxStatus));
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
    public ApiResponse buyBlindBox(@PathVariable("id") UUID blindBoxId,
                                   @RequestParam(defaultValue = "1") int quantity) {
        return ApiResponse.success(blindBoxService.buyBlindBox(blindBoxId, quantity));
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
            @PathVariable UUID blindBoxId,
            @RequestParam(required = false) BlindBoxResult.ResultStatus resultStatus
    ) {
        return ApiResponse.success(blindBoxService.getAllResultsForUser(page, size ,blindBoxId,resultStatus));
    }
    @GetMapping("/results")
    public ApiResponse getAllOpenedBlindBoxForUser(
            @RequestParam(required = false, defaultValue = "0 ") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ApiResponse.success(blindBoxService.getAllOpenedBlindBoxByUser(page, size ));
    }
    @GetMapping("/all-result")
    public ApiResponse getALlResultCardOpened(
            @RequestParam(required = false, defaultValue = "0 ") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) BlindBoxResult.ResultStatus resultStatus
            ){
return ApiResponse.success(blindBoxService.getALlResultsOpened(page,size,resultStatus));
    }
    @GetMapping("/shipments")
    public ApiResponse getShipment(
            @RequestParam(required = false, defaultValue = "0 ") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false)ShippingStatus status
            ){
      return  ApiResponse.success(blindBoxService.getBlindBoxShipment(page,size,status));
    }


}
