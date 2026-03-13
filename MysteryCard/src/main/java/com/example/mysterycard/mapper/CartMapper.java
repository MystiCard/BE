package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.CartResponse;
import com.example.mysterycard.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {CardMapper.class,ListSellerMapper.class})
public interface CartMapper {
    @Mapping(target = "cardResponse",source = "listSeller.card")
    @Mapping(target = "sellResponse",source = "listSeller")
    CartResponse toCartResponse(Cart cart);
}
