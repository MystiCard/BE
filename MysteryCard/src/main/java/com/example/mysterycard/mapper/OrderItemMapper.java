package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.OrderItemResponse;
import com.example.mysterycard.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemResponse.OrderDetailResponse entityToResponse(OrderItem orderItem);
}
