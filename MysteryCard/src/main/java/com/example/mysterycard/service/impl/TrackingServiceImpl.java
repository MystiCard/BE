package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.TrackingRequest;
import com.example.mysterycard.dto.response.ShipmentResponse;
import com.example.mysterycard.dto.response.TrackingResponse;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.entity.Image;
import com.example.mysterycard.entity.Shipment;
import com.example.mysterycard.entity.Tracking;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.TrackingMapper;
import com.example.mysterycard.repository.ImageRepo;
import com.example.mysterycard.repository.ShipmentRepo;
import com.example.mysterycard.repository.TrackingRepo;
import com.example.mysterycard.service.TrackingService;
import com.example.mysterycard.utils.CloudiaryUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl  implements TrackingService {
    private final CloudiaryUtils cloudiaryUtils;
    private final TrackingMapper trackingMapper;
    private final TrackingRepo trackingRepo;
    private final ShipmentRepo shipmentRepo;
    private final ImageRepo imageRepo;
    @Override
    @Transactional
    public TrackingResponse createTracking(TrackingRequest request) {
        Shipment shipment = shipmentRepo.findById(request.getShipmentId()).orElseThrow(
                () -> new AppException(ErrorCode.SHIPMENT_NOT_FOUND)
        );
        Tracking tracking = Tracking.builder()
                .note(request.getNote())
                .shipment(shipment)
                .shippingStatus(shipment.getShipmentStatus())
                .build();
        trackingRepo.save(tracking);
        if(request.getFileList() != null)
        {
            request.getFileList().forEach(file -> {
                Image image =Image.builder()
                        .tracking(tracking)
                        .imageUrl(cloudiaryUtils.uploadImage(file))
                        .build();
                imageRepo.save(image);
            });
        }
        return trackingMapper.entityToResponse(tracking);
    }
    @Override
    public List<TrackingResponse> getByShipmentId(UUID shipmentId) {
        Shipment shipment = shipmentRepo.findById(shipmentId).orElseThrow(
                () -> new AppException(ErrorCode.SHIPMENT_NOT_FOUND)
        );
        if(shipment.getTrackingList() == null || shipment.getTrackingList().isEmpty())
        {
            throw new AppException(ErrorCode.TRACKING_NOT_FOUND);
        }
        return shipment.getTrackingList().stream().map(trackingMapper::entityToResponse).collect(Collectors.toList());
    }
}
