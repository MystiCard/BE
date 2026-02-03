package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.AsignShipperRequest;
import com.example.mysterycard.dto.request.ShipmentRequest;
import com.example.mysterycard.dto.request.UpdateShipmentRequest;
import com.example.mysterycard.dto.response.ShipmentResponse;
import com.example.mysterycard.entity.Order;
import com.example.mysterycard.enums.ShippingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ShipmentService {
    ShipmentResponse createsShipment(ShipmentRequest shipmentRequest);

    List<ShipmentResponse> getShipmentByOrder(UUID orderId);

    Page<ShipmentResponse> myShipment(boolean complete, int page, int size);

    Page<ShipmentResponse> shipmentNotAsigned(int page, int size);

    ShipmentResponse asignShipper(AsignShipperRequest request);

    ShipmentResponse update(UpdateShipmentRequest request, List<MultipartFile> list);
}

