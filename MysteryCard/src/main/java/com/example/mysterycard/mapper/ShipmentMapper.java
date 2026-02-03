package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.ShipmentRequest;
import com.example.mysterycard.dto.response.ShipmentResponse;
import com.example.mysterycard.entity.Shipment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {
    ShipmentResponse entityToResponse(Shipment shipment);
    Shipment requestToEntity(ShipmentRequest shipmentRequest);
}
