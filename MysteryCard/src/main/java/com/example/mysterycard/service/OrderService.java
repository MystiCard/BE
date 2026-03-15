package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.MyOrderDetailRequest;
import com.example.mysterycard.dto.request.OrderBlinkBoxResultRequest;
import com.example.mysterycard.dto.request.OrderCardRequest;
import com.example.mysterycard.dto.response.*;
import com.example.mysterycard.entity.OrderItem;
import com.example.mysterycard.entity.Shipment;
import com.example.mysterycard.enums.OrderItemStatus;
import com.example.mysterycard.enums.OrderStatus;
import com.example.mysterycard.enums.ShippingStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderCardResponse createOrder(OrderCardRequest orderCardRequest);
    PageResponse<OrderItemResponse> getByStatusShipment(MyOrderDetailRequest request, int page, int size);
    OrderItemResponse confirmReceiveCard(UUID orderItemId);
    OrderItemResponse.OrderDetailResponse cancleOrderItem(UUID orderItemId);
    OrderCardResponse cancleOrder(UUID orderId);
    PageResponse<OrderItemResponse> getMyReturnOrderItem(ShippingStatus shippingStatus, int page, int size);
    Page<OrderResponse> getMyOrders(OrderStatus orderStatus, int page, int size);
    boolean canCanleOrder(UUID orderId);
    boolean canCancleOrderDetail(UUID orderDetailIDd);
    boolean canConfirmShipment(UUID shipmentID);
//    OrderCanDoResponse canCancleOrComfirm(UUID orderItemId);
    Page<OrderItemResponse.OrderDetailResponse> listPending(int page, int size);
    OrderItemResponse.OrderDetailResponse approvedOrderItems(UUID orderItemId);
    OrderBlindBoxResultResponse createBlindBoxOrder(OrderBlinkBoxResultRequest request);
}
