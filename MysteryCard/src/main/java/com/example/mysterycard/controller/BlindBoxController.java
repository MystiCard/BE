package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.OrderBlinkBoxResultRequest;
import com.example.mysterycard.dto.response.BlindBoxProbabilitiesResponse;
import com.example.mysterycard.dto.response.ShipmentResponse;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.service.BlindBoxService;
import com.example.mysterycard.service.OrderService;
import com.example.mysterycard.service.ShipmentService;
import com.example.mysterycard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/blind-boxes")
@RequiredArgsConstructor
public class BlindBoxController {

    private final BlindBoxService blindBoxService;
    private final OrderService orderService;
    private final ShipmentService shipmentService;
    private final UserService userService;

    @PostMapping
    public ApiResponse<?> createBlindBox(@RequestBody com.example.mysterycard.dto.request.BlindBoxRequest request) {
        return ApiResponse.success(blindBoxService.createBlindBox(request));
    }

    @GetMapping("/{id}/draw-card")
    public ApiResponse<?> drawCard(@PathVariable("id") UUID id) {
        return ApiResponse.success(blindBoxService.drawCard(id));
    }

    @GetMapping
    public ApiResponse<?> getAllBlindBoxes() {
        return ApiResponse.success(blindBoxService.getAllBlindBoxes());
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getBlindBoxById(@PathVariable("id") UUID id) {
        return ApiResponse.success(blindBoxService.getBlindBoxById(id));
    }

    @GetMapping("/{id}/cards")
    public ApiResponse<?> getCardsInBlindBox(@PathVariable("id") UUID blindBoxId) {
        return ApiResponse.success(blindBoxService.getCardsInBlindBox(blindBoxId));
    }

    @PostMapping("/{id}/buy")
    public ApiResponse<?> buyBlindBox(@PathVariable("id") UUID blindBoxId) {
        return ApiResponse.success(blindBoxService.buyBlindBox(blindBoxId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteBlindBox(@PathVariable("id") UUID id) {
        blindBoxService.deleteBlindBox(id);
        return ApiResponse.success();
    }

    @GetMapping("/{id}/probabilities")
    public BlindBoxProbabilitiesResponse getProbabilities(@PathVariable UUID id) {
        return blindBoxService.getProbabilities(id);
    }

    @GetMapping("/results")
    public ApiResponse<?> getAllResultsForUser(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ApiResponse.success(blindBoxService.getAllResultsForUser(page, size));
    }

    /**
     * Yêu cầu ship các thẻ đã mở (BlindBoxResult) về nhà cho user hiện tại.
     * FE: POST /api/blind-boxes/me/ship với body là mảng UUID blindBoxResultId.
     * - Lấy địa chỉ / phone / district / ward từ user hiện tại
     * - Gọi OrderService.createBlindBoxOrder để tạo Order + Shipment
     * - Lấy Shipment vừa tạo thông qua ShipmentService.getShipmentByBlindBoxResult và trả về cho FE
     */
    @PostMapping("/me/ship")
    public ApiResponse<ShipmentResponse> requestShipResults(@RequestBody List<UUID> blindBoxResultIds) {
        if (blindBoxResultIds == null || blindBoxResultIds.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        Users user = userService.getUser();
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if (user.getAddress() == null || user.getPhone() == null
                || user.getDistrictId() == null || user.getWardId() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        OrderBlinkBoxResultRequest request = new OrderBlinkBoxResultRequest();
        request.setBuyerAddress(user.getAddress());
        request.setBuyerPhone(user.getPhone());
        request.setToDistrictId(Long.valueOf(user.getDistrictId()));
        request.setToWardId(Long.valueOf(user.getWardId()));
        request.setBlindBoxResultIds(blindBoxResultIds);

        // Tạo Order + Shipment cho các BlindBoxResult đã chọn
        orderService.createBlindBoxOrder(request);

        // Lấy Shipment vừa tạo từ 1 trong các BlindBoxResult
        UUID sampleResultId = blindBoxResultIds.get(0);
        var shipments = shipmentService.getShipmentByBlindBoxResult(sampleResultId);
        if (shipments == null || shipments.isEmpty()) {
            throw new AppException(ErrorCode.SHIPMENT_NOT_FOUND);
        }

        ShipmentResponse shipmentResponse = shipments.get(0);
        return ApiResponse.success(shipmentResponse);
    }

}
