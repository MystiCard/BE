package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.BlindBoxCardResponse;
import com.example.mysterycard.entity.BlindBoxCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {CardMapper.class})
public interface BlindBoxCardMapper {
    @Mapping(source = "blindBox.blindBoxId", target = "blindBoxId")
    @Mapping(source = "card",target ="cardResponse" )
    BlindBoxCardResponse toBlindBoxCardResponse(BlindBoxCard blindBoxCard);
}
