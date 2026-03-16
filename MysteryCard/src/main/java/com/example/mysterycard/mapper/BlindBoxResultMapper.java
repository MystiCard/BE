package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.BlindBoxResultResponse;
import com.example.mysterycard.entity.BlindBoxResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlindBoxResultMapper {
    @Mapping(source = "card.name" , target = "cardName")
    BlindBoxResultResponse toResponse (BlindBoxResult result);
}
