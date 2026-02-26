package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.OrderCardRequest;
import com.example.mysterycard.dto.response.OrderCardResponse;
import com.example.mysterycard.dto.response.OrderItemResponse;
import com.example.mysterycard.dto.response.PageResponse;
import com.example.mysterycard.dto.response.ShipmentResponse;
import com.example.mysterycard.enums.ShippingStatus;
import com.example.mysterycard.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderCardResponse>> createOrder(
            @RequestBody @Valid OrderCardRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(orderService.createOrder(request)));
    }

    @PostMapping("/status")
    public ResponseEntity<ApiResponse<PageResponse<OrderItemResponse>>> getOrderStatus(
            @RequestBody ShippingStatus shippingStatus,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getByStatusShipment(shippingStatus, page, size)));
    }

    @PostMapping("/confirm-receive/{shipmentId}")
    public ResponseEntity<ApiResponse<OrderItemResponse>> confirmReceiveCard(
            @PathVariable UUID shipmentId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.confirmReceiveCard(shipmentId)));
    }

    @PostMapping("/cancel/{orderItemId}")
    public ResponseEntity<ApiResponse<OrderItemResponse.OrderDetailResponse>> cancelOrderItem(
            @PathVariable UUID orderItemId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.cancleOrderItem(orderItemId)));
    }

    @PostMapping("/cancel-order/{orderId}")
    public ResponseEntity<ApiResponse<OrderCardResponse>> cancelOrder(
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.cancleOrder(orderId)));

    }
    @PostMapping("/my-card-return")
    public ResponseEntity<ApiResponse<PageResponse<OrderItemResponse>>> getMyReturnOrderItem(
            @RequestBody ShippingStatus shippingStatus,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getMyReturnOrderItem(shippingStatus, page, size)));
    }
}