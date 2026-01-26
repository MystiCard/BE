package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.BlindBoxCardResponse;
import com.example.mysterycard.entity.BlindBoxCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlindBoxCardMapper {
    @Mapping(source = "card.name", target = "cardName")
    @Mapping(source = "blindBox.blindBoxId", target = "blindBoxId")
    @Mapping(source = "card.cardId", target = "cardId")
    @Mapping(source = "card.rarity", target = "rarity")
    BlindBoxCardResponse toBlindBoxCardResponse(BlindBoxCard blindBoxCard);
}
