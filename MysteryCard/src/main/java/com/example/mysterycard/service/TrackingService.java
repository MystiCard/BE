package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.TrackingRequest;
import com.example.mysterycard.dto.response.TrackingResponse;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.entity.Shipment;

import java.util.List;
import java.util.UUID;

public interface TrackingService {
    TrackingResponse createTracking(TrackingRequest request);
    List<TrackingResponse> getByShipmentId(UUID shipmentId);
}
