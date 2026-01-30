package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.BlindBoxRequest;
import com.example.mysterycard.dto.response.BlindBoxResponse;
import com.example.mysterycard.entity.BlindBox;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BlindBoxMapper {
    BlindBox toBlindBox(BlindBoxRequest request);
    BlindBoxResponse toBlindBoxResponse(BlindBox blindBox);
}
