package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.ChangeAddressShipmentRequest;
import com.example.mysterycard.dto.request.ShipmentRequest;
import com.example.mysterycard.dto.response.ShipmentResponse;
import com.example.mysterycard.entity.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {
    @Mapping(target = "trackingResponses",source = "trackingList")
    ShipmentResponse entityToResponse(Shipment shipment);
    Shipment requestToEntity(ShipmentRequest shipmentRequest);
}
