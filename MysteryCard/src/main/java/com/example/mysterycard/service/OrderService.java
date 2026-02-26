package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.OrderCardRequest;
import com.example.mysterycard.dto.response.OrderCardResponse;
import com.example.mysterycard.dto.response.OrderItemResponse;
import com.example.mysterycard.dto.response.PageResponse;
import com.example.mysterycard.enums.ShippingStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderCardResponse createOrder(OrderCardRequest orderCardRequest);
    PageResponse<OrderItemResponse> getByStatusShipment(ShippingStatus shippingStatus, int page, int size);
    OrderItemResponse confirmReceiveCard(UUID orderItemId);
    OrderItemResponse.OrderDetailResponse cancleOrderItem(UUID orderItemId);
    OrderCardResponse cancleOrder(UUID orderId);
    PageResponse<OrderItemResponse> getMyReturnOrderItem(ShippingStatus shippingStatus, int page, int size);
}
