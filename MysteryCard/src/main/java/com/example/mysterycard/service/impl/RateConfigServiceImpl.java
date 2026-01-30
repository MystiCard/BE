package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.RateConfigRequest;
import com.example.mysterycard.dto.response.RateConfigResponse;
import com.example.mysterycard.entity.RateConfig;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.RateConfigMapper;
import com.example.mysterycard.repository.RateConfigRepo;
import com.example.mysterycard.service.RateConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RateConfigServiceImpl implements RateConfigService {
    @Autowired
    private RateConfigRepo rateConfigRepo;
    @Autowired
    private RateConfigMapper rateConfigMapper;
    @Override
    public RateConfigResponse createRateConfig(RateConfigRequest request) {
        if(rateConfigRepo.existsByCardRarity(request.getCardRarity())){
            throw new AppException(ErrorCode.RATE_CONFIG_ALREADY_EXISTS);
        }
        RateConfig rateConfig = rateConfigMapper.toEntity(request);
        rateConfig = rateConfigRepo.save(rateConfig);
        return rateConfigMapper.toResponse(rateConfig);
    }

    @Override
    public List<RateConfigResponse> getAllRateConfigs() {
       List<RateConfig> rateConfigs = rateConfigRepo.findAll();
         return rateConfigs.stream()
                .map(rateConfigMapper::toResponse)
                .toList();
    }

    @Override
    public RateConfigResponse updateRateConfig(UUID id, RateConfigRequest request) {
        RateConfig rateConfig = rateConfigRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.RATE_CONFIG_NOT_FOUND));
        rateConfig.setDropRate(request.getDropRate());
        rateConfig = rateConfigRepo.save(rateConfig);
        return rateConfigMapper.toResponse(rateConfig);
    }

    @Override
    public RateConfigResponse getRateConfigById(UUID id) {
        RateConfig rateConfig = rateConfigRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.RATE_CONFIG_NOT_FOUND));
        return rateConfigMapper.toResponse(rateConfig);
    }

    @Override
    public void deleteRateConfig(UUID id) {
        RateConfig rateConfig = rateConfigRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.RATE_CONFIG_NOT_FOUND));
        rateConfigRepo.delete(rateConfig);
    }
}
