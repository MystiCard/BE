package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.WishListRequest;
import com.example.mysterycard.dto.response.WishListResponse;
import com.example.mysterycard.entity.WishList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {CardMapper.class})
public interface WishListMapper {
    WishList toEntity(WishListRequest request);
    @Mapping(target = "cardResponse", source = "card")
    WishListResponse toResponse(WishList wishList);
}
