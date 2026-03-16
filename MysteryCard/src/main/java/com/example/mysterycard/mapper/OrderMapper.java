package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.OrderCardResponse;
import com.example.mysterycard.dto.response.OrderResponse;
import com.example.mysterycard.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "buyer.userId", target = "buyerId")
    OrderResponse toOrderResponse(Order order);
    OrderCardResponse entityToResponse(Order order);
}
