package com.example.mysterycard.dto.response;

import com.example.mysterycard.entity.*;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class BlindBoxShipmentResponse {
    private ShipmentResponse shipmentResponse;
    private UUID orderId;
    private List<BlinboxShipDetail> blinboxShipDetail;
    @Builder
    @Data
    public static class BlinboxShipDetail{
        private UUID blindBoxResultId;
        private LocalDateTime openedAt;
        private BlindBoxResult.ResultStatus status;
        private CardResponse cardResponse;
    }


}
