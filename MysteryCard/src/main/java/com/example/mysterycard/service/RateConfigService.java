package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.RateConfigRequest;
import com.example.mysterycard.dto.response.RateConfigResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RateConfigService {
    RateConfigResponse createRateConfig(RateConfigRequest request);
    List<RateConfigResponse> getAllRateConfigs();
    RateConfigResponse updateRateConfig(UUID id , RateConfigRequest request);
    RateConfigResponse getRateConfigById(UUID id);
    void deleteRateConfig(UUID id);
    Map<String, Integer> importRateConfigs(MultipartFile file);
}
