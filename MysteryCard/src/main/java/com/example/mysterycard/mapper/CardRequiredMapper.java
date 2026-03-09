package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.CardRequiredResponse;
import com.example.mysterycard.entity.CardRequired;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardRequiredMapper {
    @Mapping(source = "users.name" , target ="userName" )
    @Mapping(source = "category.categoryName", target = "categoryName")
    CardRequiredResponse toResponse(CardRequired cardRequired);
}
