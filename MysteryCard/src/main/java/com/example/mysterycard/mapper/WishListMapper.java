package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.WishListRequest;
import com.example.mysterycard.dto.response.WishListResponse;
import com.example.mysterycard.entity.WishList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WishListMapper {
    WishList toEntity(WishListRequest request);
    @Mapping(source = "card.cardId", target = "cardId")
    @Mapping(source = "user.userId", target = "userId")
    WishListResponse toResponse(WishList wishList);
}
