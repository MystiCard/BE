package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.PermisionResponse;
import com.example.mysterycard.entity.Permision;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermisionMapper {
    PermisionResponse entityToResponse(Permision permision);
}
