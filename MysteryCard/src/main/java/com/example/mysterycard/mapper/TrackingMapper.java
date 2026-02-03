package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.TrackingResponse;
import com.example.mysterycard.entity.Tracking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {ImageMapper.class})
public interface TrackingMapper {
    @Mapping(target = "images",source = "images")
    TrackingResponse entityToResponse(Tracking tracking);
}
