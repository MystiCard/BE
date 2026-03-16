package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.ReturnRequestdto;
import com.example.mysterycard.dto.response.ReturnResponse;
import com.example.mysterycard.entity.ReturnRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {OrderItemMapper.class,ImageMapper.class, ShipmentMapper.class})
public interface ReturnRequestMapper {
    @Mapping(source = "orderItemList", target = "orderItems")
    @Mapping(target = "listImages",source = "images")
    @Mapping(target = "shipmentResponse",source = "shipment")
    ReturnResponse enityToReturnResponse(ReturnRequest returnRequest);
    ReturnRequest requestToEntity(ReturnRequestdto request);
}
