package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.BlindBoxPurChaseResponse;
import com.example.mysterycard.entity.BlindBoxPurChase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlindBoxPurChaseMapper {
    @Mapping(source = "buyer.name", target = "buyer")
    @Mapping(source = "blindBox.name", target = "blindBoxName")
    BlindBoxPurChaseResponse toResponse(BlindBoxPurChase blindBoxPurChase);
}
