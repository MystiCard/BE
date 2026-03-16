package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.MyOrderDetailRequest;
import com.example.mysterycard.dto.request.OrderBlinkBoxResultRequest;
import com.example.mysterycard.dto.request.OrderCardRequest;
import com.example.mysterycard.dto.response.*;
import com.example.mysterycard.enums.OrderItemStatus;
import com.example.mysterycard.enums.OrderStatus;
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
    @PostMapping("/create-blind-box")
    public ResponseEntity<ApiResponse<OrderBlindBoxResultResponse>> createOrderBlindBox(
            @RequestBody @Valid OrderBlinkBoxResultRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(orderService.createBlindBoxOrder(request)));
    }

    @PostMapping("/orderItems/status")
    public ResponseEntity<ApiResponse<PageResponse<OrderItemResponse>>> getOrderStatus(
            @RequestBody MyOrderDetailRequest request,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getByStatusShipment(request, page, size)));
    }

    @PostMapping("/confirm-receive/{shipmentId}")
    public ResponseEntity<ApiResponse<OrderItemResponse>> confirmReceiveCard(
            @PathVariable UUID shipmentId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.confirmReceiveCard(shipmentId)));
    }
    @PostMapping("/confirm-receive-results/{shipmentId}")
    public ResponseEntity<ApiResponse<ConfirmBlindBoxResponse>> confirmReceiveBlindBoxResults(
            @PathVariable UUID shipmentId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.confirmReceiveBlindBoxResults(shipmentId)));
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
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size ){

        return ResponseEntity.ok(ApiResponse.success(orderService.getMyOrders(orderStatus,page,size)));
    }
    @GetMapping("/pendings")
    public ResponseEntity<ApiResponse<Page<OrderItemResponse.OrderDetailResponse>>> listPending(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    )
    {
        return ResponseEntity.ok(ApiResponse.success(orderService.listPending(page-1,size)));
    }
    @PostMapping ("/aproved/{orderItemdid}")
    public ResponseEntity<ApiResponse<OrderItemResponse.OrderDetailResponse>> listPending(
            @PathVariable UUID orderItemdid
    )
    {
        return ResponseEntity.ok(ApiResponse.success(orderService.approvedOrderItems(orderItemdid)));
    }
    @GetMapping("/can-do/{orderItemId}")
    public ResponseEntity<ApiResponse<OrderCanDoResponse>> canConfirmOrCancle(@PathVariable UUID orderItemId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.canCancleOrComfirm(orderItemId)));
    }
    @GetMapping("/can-cancle-order/{orderId}")
    public ResponseEntity<ApiResponse<?>> canCancleOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.canCanleOrder(orderId)));
    }
}