package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.RateConfigRequest;
import com.example.mysterycard.service.RateConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/rate-config")
public class RateConfigController {
    @Autowired
    private RateConfigService rateConfigService;

    @GetMapping
    public ApiResponse getRateConfigs() {
        return ApiResponse.success(rateConfigService.getAllRateConfigs());
    }

    @GetMapping("/{id}")
    public ApiResponse getRateConfigById(@PathVariable UUID id) {
        return ApiResponse.success(rateConfigService.getRateConfigById(id));
    }

    @PostMapping
    public ApiResponse createRateConfig(@RequestBody RateConfigRequest request) {
        return ApiResponse.success(rateConfigService.createRateConfig(request));
    }

    @PutMapping("/{id}")
    public ApiResponse updateRateConfig(@PathVariable UUID id, @RequestBody RateConfigRequest
    request) {
            return ApiResponse.success(rateConfigService.updateRateConfig(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteRateConfig(@PathVariable UUID id) {
        rateConfigService.deleteRateConfig(id);
        return ApiResponse.success();
    }
    @PostMapping("/import")
    public ApiResponse importCategories(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success(rateConfigService.importRateConfigs(file));
    }
}
