package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.OrderItemResponse;
import com.example.mysterycard.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {CardMapper.class})
public interface OrderItemMapper {
    @Mapping(target = "cardResponse",  source= "listSeller.card")
    OrderItemResponse.OrderDetailResponse entityToResponse(OrderItem orderItem);
}
