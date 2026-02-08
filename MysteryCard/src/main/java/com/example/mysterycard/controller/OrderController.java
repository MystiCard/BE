package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.OrderCardRequest;
import com.example.mysterycard.dto.response.OrderCardResponse;
import com.example.mysterycard.dto.response.ShipmentResponse;
import com.example.mysterycard.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
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
            @RequestBody @Valid OrderCardRequest request
            ){
        return ResponseEntity.ok(ApiResponse.success(orderService.createOrder(request)));
    }
}
