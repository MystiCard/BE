package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.SellRequest;
import com.example.mysterycard.dto.response.SellResponse;
import com.example.mysterycard.entity.ListSeller;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ListSellerMapper {
    ListSeller toEntity(SellRequest listSeller);
    @Mapping(source = "card.cardId", target = "cardId")
    @Mapping(source = "seller.userId", target = "sellerId")
    SellResponse toResponse(ListSeller listSeller);
}
