package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.OrderCardResponse;
import com.example.mysterycard.dto.response.OrderResponse;
import com.example.mysterycard.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(source = "buyer.userId", target = "buyerId")
    @Mapping(source = "blindBox.blindBoxId", target = "blindBoxId")
    OrderResponse toOrderResponse(Order order);
    @Mapping(target = "orderItems",source = "orderItemList")
    OrderCardResponse entityToResponse(Order order);
}
