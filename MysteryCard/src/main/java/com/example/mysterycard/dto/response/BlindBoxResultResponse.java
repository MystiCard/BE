package com.example.mysterycard.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BlindBoxResultResponse {
    private UUID blindBoxResultId;
    private LocalDateTime openedAt;
    private String cardName;
    private String cardImageUrl;
    private String rarity;
    // Các trạng thái để FE quyết định có cho phép giao về nhà nữa hay không
    private boolean shipped;                 // Đã có shipment (đã yêu cầu giao)
    private boolean shippedToHomeDelivered;  // Shipment đã giao thành công (RECEIVED/DELIVERED)
    private boolean listedForSale;           // Đang đăng bán trên sàn (future use)
    private boolean soldAndDeliveredToBuyer; // Đã bán qua sàn và buyer đã nhận (future use)
}
