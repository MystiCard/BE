package com.example.mysterycard.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ConfirmBlindBoxResponse {
    private ShipmentResponse shipmentResponse;   // thông tin shipment đã cập nhật sang RECEIVED
    private Long shipfee;                        // phí ship đã release cho shipper
    private String orderStatus;                  // trạng thái order (COMPLETED)
    private List<BlindBoxResultResponse> blindBoxResults; // danh sách kết quả blindbox đã đổi sang RECEIVED
}
