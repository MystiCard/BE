package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.CategoryResponse;
import com.example.mysterycard.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);
}
