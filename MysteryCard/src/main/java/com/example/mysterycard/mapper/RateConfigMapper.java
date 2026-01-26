package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.RateConfigRequest;
import com.example.mysterycard.dto.response.RateConfigResponse;
import com.example.mysterycard.entity.RateConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RateConfigMapper {
    RateConfig toEntity(RateConfigRequest request);
    @Mapping(source = "rateConfigId", target = "rateConfigId")
    RateConfigResponse toResponse(RateConfig entity);
}
