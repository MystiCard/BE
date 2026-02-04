package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.OrderCardRequest;
import com.example.mysterycard.dto.response.OrderCardResponse;
import com.example.mysterycard.dto.response.ShipmentResponse;
import com.example.mysterycard.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderCardResponse>> createOrder(
            @RequestBody OrderCardRequest request
            ){
        return ResponseEntity.ok(ApiResponse.success(orderService.createOrder(request)));
    }
}
