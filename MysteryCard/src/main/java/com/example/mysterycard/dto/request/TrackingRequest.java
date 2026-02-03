package com.example.mysterycard.dto.request;

import com.example.mysterycard.entity.Shipment;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TrackingRequest {
    private List<MultipartFile> fileList ;
    private UUID shipmentId;
    private String note;
}
