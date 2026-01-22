package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.CardRequest;
import com.example.mysterycard.dto.response.CardResponse;
import com.example.mysterycard.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardMapper {
    Card toCard(CardRequest cardRequest);
    @Mapping(source = "category.categoryName", target = "categoryName")
    CardResponse toResponse(Card card);
}
