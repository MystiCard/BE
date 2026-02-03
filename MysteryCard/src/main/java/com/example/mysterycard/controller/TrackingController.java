package com.example.mysterycard.controller;

import com.cloudinary.Api;
import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.response.TrackingResponse;
import com.example.mysterycard.entity.Tracking;
import com.example.mysterycard.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/trackings")
public class TrackingController {
    private final TrackingService trackingService;
    @GetMapping("/shipments/{shipmentId}")
    public ResponseEntity<ApiResponse<List<TrackingResponse>>> getByShipmentId(@PathVariable UUID shipmentId) {
        return ResponseEntity.ok(ApiResponse.success(trackingService.getByShipmentId(shipmentId)));
    }
}
