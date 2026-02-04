package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.OrderCardRequest;
import com.example.mysterycard.dto.response.OrderCardResponse;

public interface OrderService {

    OrderCardResponse createOrder(OrderCardRequest orderCardRequest);
}
