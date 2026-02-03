package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.ImageResponse;
import com.example.mysterycard.entity.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageResponse entityToResponse(Image image);
}
