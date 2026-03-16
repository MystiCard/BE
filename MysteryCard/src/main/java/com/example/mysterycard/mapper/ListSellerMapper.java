package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.SellRequest;
import com.example.mysterycard.dto.response.ListSellerResponse;
import com.example.mysterycard.dto.response.SellResponse;
import com.example.mysterycard.entity.ListSeller;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CardMapper.class})
public interface ListSellerMapper {
    ListSeller toEntity(SellRequest listSeller);
    @Mapping(source = "card.cardId", target = "cardId")
    @Mapping(source = "seller.userId", target = "sellerId")
    @Mapping(target = "sellerName", source = "seller.name")
    SellResponse toResponse(ListSeller listSeller);

    @Mapping(target = "cardResponse",source = "card")
    @Mapping(target = "sellerResponse",source = "seller")
    ListSellerResponse entityToResponse(ListSeller listSeller);
}
