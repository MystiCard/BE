package com.example.mysterycard.dto.response;

import com.example.mysterycard.entity.Image;
import com.example.mysterycard.enums.ShippingStatus;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class TrackingResponse {
    private UUID trackingId;
    private LocalDateTime createAt ;
    private ShippingStatus shippingStatus;
    private List<ImageResponse> images ;
    private String note;
}
