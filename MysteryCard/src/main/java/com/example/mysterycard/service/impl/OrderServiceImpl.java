package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.CalculateFeeRequest;
import com.example.mysterycard.dto.request.OrderCardRequest;
import com.example.mysterycard.dto.request.ShipmentRequest;
import com.example.mysterycard.dto.request.UpdateShipmentRequest;
import com.example.mysterycard.dto.response.OrderCardResponse;
import com.example.mysterycard.dto.response.OrderItemResponse;
import com.example.mysterycard.dto.response.PageResponse;
import com.example.mysterycard.entity.*;
import com.example.mysterycard.enums.OrderItemStatus;
import com.example.mysterycard.enums.OrderStatus;
import com.example.mysterycard.enums.ReturnRequestStatus;
import com.example.mysterycard.enums.ShippingStatus;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.OrderItemMapper;
import com.example.mysterycard.mapper.OrderMapper;
import com.example.mysterycard.mapper.ShipmentMapper;
import com.example.mysterycard.repository.*;
import com.example.mysterycard.service.OrderService;
import com.example.mysterycard.service.ShipmentService;
import com.example.mysterycard.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepo orderRepo;
    private final OrderItemsRepo orderItemsRepo;
    private final ShipmentService shipmentService;
    private final UsersRepo usersRepo;
    private final ListSellerRepo listSellerRepo;
    private final ShipmentRepo shipmentRepo;
    private final OrderItemMapper orderItemMapper;
    private final ShipmentMapper shipmentMapper;
    private final TransactionService transactionService;
    private final ReturnRequestRepo returnRequestRepo;
    @Override
    @Transactional
    public OrderCardResponse createOrder(OrderCardRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepo.findByEmail(email);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Order order = Order.builder()
                .buyer(users)
                .build();
        order = orderRepo.save(order);
        order.setOrderItemList(new ArrayList<>());
        double totalAmount = 0;
        List<OrderItemResponse> allOrderItemsResponse = new ArrayList<>();
        if (request.getOrderItemsList() != null && !request.getOrderItemsList().isEmpty()) {

            Map<UUID, List<OrderCardRequest.OrderItems>> groupedBySeller = request.getOrderItemsList().stream()
                    .collect(Collectors.groupingBy(item -> {
                        ListSeller ls = listSellerRepo.findById(item.getListSellerId())
                                .orElseThrow(() -> new AppException(ErrorCode.LIST_SELLER_NOT_FOUND));
                        return ls.getSeller().getUserId();
                    }));

            for (Map.Entry<UUID, List<OrderCardRequest.OrderItems>> orderItems : groupedBySeller.entrySet()) {
                List<OrderItem> orderItemList = new ArrayList<>();
                ListSeller listSeller = listSellerRepo.findById(orderItems.getValue().getFirst().getListSellerId())
                        .orElseThrow(() -> new AppException(ErrorCode.LIST_SELLER_NOT_FOUND));
                double totalOrderItems = 0L;
                List<UUID> orderItemIds = new ArrayList<>();
                for (OrderCardRequest.OrderItems item : orderItems.getValue()) {
                    log.info("Item {}", item.toString());
                    ListSeller ls = listSellerRepo.findById(item.getListSellerId())
                            .orElseThrow(() -> new AppException(ErrorCode.LIST_SELLER_NOT_FOUND));
                    if (item.getQuantity() > ls.getQuantity()) {
                        throw new AppException(ErrorCode.QUANTITY_OVER_AVAIABLE);
                    }

                    OrderItem orderIt = OrderItem.builder()
                            .quantity(item.getQuantity())
                            .listSeller(ls)
                            .order(order)
                            .price(ls.getPrice())
                            .build();
                    order.getOrderItemList().add(orderIt);
                    orderItemList.add(orderItemsRepo.save(orderIt));
                    orderItemIds.add(orderIt.getOrderItemId());
                    double price = orderIt.getQuantity() * ls.getPrice();
                    totalOrderItems += price;
                    totalAmount += price;
                }
                Long shipfee = shipmentService.calculatFeeShip(
                        CalculateFeeRequest.builder()
                                .totalAmount(totalOrderItems)
                                .fromDistrictId(Long.valueOf(listSeller.getSeller().getDistrictId()))
                                .toWardId(String.valueOf(request.getToWardId()))
                                .toDistrictId(request.getToDistrictId())
                                .build()
                );
                log.info("Calculator ship  {}", CalculateFeeRequest.builder()
                        .totalAmount(totalOrderItems)
                        .fromDistrictId(Long.valueOf(listSeller.getSeller().getDistrictId()))
                        .toWardId(String.valueOf(request.getToWardId()))
                        .toDistrictId(request.getToDistrictId())
                        .build().toString());
                totalAmount += shipfee;
                OrderItemResponse orderItemResponse = OrderItemResponse.builder()
                        .orderDetailResponseList(orderItemList.stream().map(orderItemMapper::entityToResponse).toList())
                        .shipfee(shipfee)
                        .shipmentResponse(shipmentService.createsShipment(
                                ShipmentRequest.builder()
                                        .orderItemId(orderItemIds)
                                        .toAddress(request.getBuyerAddress())
                                        .toDistrictId(request.getToDistrictId())
                                        .toWardId(request.getToWardId())
                                        .toPhone(request.getBuyerPhone())
                                        .fromPhone(listSeller.getSeller().getPhone())
                                        .fromAddress(listSeller.getSeller().getAddress())
                                        .fromDistrictId(Long.valueOf(listSeller.getSeller().getDistrictId()))
                                        .shipmentFee(shipfee)
                                        .build()
                        ))
                        .build();
                allOrderItemsResponse.add(orderItemResponse);
            }
        }
        order.setTotalAmount(totalAmount);
        OrderCardResponse orderCardResponse = orderMapper.entityToResponse(orderRepo.save(order));
        orderCardResponse.setOrderItems(allOrderItemsResponse);
        return orderCardResponse;
    }

    @Override
    public PageResponse<OrderItemResponse> getByStatusShipment(ShippingStatus shippingStatus, int page, int size) {
        page = page - 1;
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepo.findByEmail(name);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        List<OrderItemResponse> result = new ArrayList<>();
        List<Shipment> shipments = shipmentRepo.findByShipmentStatus(shippingStatus);
        shipments.forEach(shipment -> {
            List<OrderItem> orderItems = new ArrayList<>();
            shipment.getOrderItems().forEach(orderItem -> {
                if (orderItem.getOrder().getBuyer().getUserId().equals(users.getUserId())) {
                    orderItems.add(orderItem);
                }
            });
            result.add(
                    OrderItemResponse.builder()
                            .shipmentResponse(shipmentMapper.entityToResponse(shipment))
                            .shipfee(shipment.getShipmentFee())
                            .orderDetailResponseList(orderItems.stream().map(orderItemMapper::entityToResponse).toList())
                            .build()
            );
        });
        int totalPages = result.size() / size;
        int from = page * size;
        int to = Math.min(((page + 1) * size), result.size());
        List<OrderItemResponse> orderItems = new ArrayList<>();
        if(from < to) {
            orderItems = result.stream().toList().subList(from, to);
        }
        PageResponse<OrderItemResponse> pageResponse = PageResponse.<OrderItemResponse>builder()
                .content(orderItems)
                .totalElements(result.size())
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .last(to == result.size())
                .build();
        return pageResponse;
    }

    @Override
    @Transactional
    public OrderItemResponse confirmReceiveCard(UUID shipmentId) {
        Shipment shipment = shipmentRepo.findById(shipmentId).orElseThrow(
                () -> new AppException(ErrorCode.SHIPMENT_NOT_FOUND)
        );
        if(!shipment.getShipmentStatus().equals(ShippingStatus.DELIVERED))
        {
            throw new AppException(ErrorCode.CAN_NOT_CONFIRM_RECEIVE);
        }
        shipmentService.update(UpdateShipmentRequest.builder()
                .shippingStatus(ShippingStatus.RECEIVED)
                .shipmentId(shipmentId)
                .build(), null);
        for (OrderItem orderItem : new ArrayList<>(shipment.getOrderItems())) {
            if (orderItem.getOrderItemStatus().equals(OrderItemStatus.CONFIRMED)) {
                orderItem.setOrderItemStatus(OrderItemStatus.RECIEVED);
                transactionService.releasePrice(orderItem);
            } else if(orderItem.getOrderItemStatus().equals(OrderItemStatus.RETURNING)) {
                orderItem.setOrderItemStatus(OrderItemStatus.RETURNED);
                transactionService.releasePrice(orderItem);
            }
        }
        Order order = shipment.getOrderItems().stream().toList().getFirst().getOrder();
        int countReceived = 0;
        int countCancelled = 0;
        for (OrderItem item : order.getOrderItemList()) {
            Shipment ship = item.getShipments().stream().toList().getLast();
            if (ship.getShipmentStatus().equals(ShippingStatus.RECEIVED)) {
                countReceived++;
            }
            if (item.getOrderItemStatus().equals(OrderItemStatus.CANCELLED)) {
                countCancelled++;
            }
        }
        if (countReceived == (order.getOrderItemList().size() - countCancelled)) {
            order.setStatus(OrderStatus.COMPLETED);
        } else if (countReceived > 0) {
            order.setStatus(OrderStatus.PARTIAL_COMPLETED);

        }
        orderRepo.save(order);
        return OrderItemResponse.builder()
                .shipmentResponse(shipmentMapper.entityToResponse(shipment))
                .shipfee(shipment.getShipmentFee())
                .orderDetailResponseList(shipment.getOrderItems().stream().map(orderItemMapper::entityToResponse).toList())
                .build();
    }

    @Override
    @Transactional
    public OrderItemResponse.OrderDetailResponse cancleOrderItem(UUID orderItemId) {
        OrderItem orderItem = orderItemsRepo.findById(orderItemId).orElseThrow(
                () -> new AppException(ErrorCode.ORDER_ITEMS_NOT_FOUND)
        );
        ShippingStatus shippingStatus = orderItem.getShipments().stream().toList().getLast().getShipmentStatus();
        if (!shippingStatus.equals(ShippingStatus.PENDING) || orderItem.getOrderItemStatus().equals(OrderItemStatus.CANCELLED)) {
            throw new AppException(ErrorCode.CAN_NOT_CANCEL_ORDER_ITEM);
        }
        orderItem.setOrderItemStatus(OrderItemStatus.CANCELLED);
        orderItemsRepo.save(orderItem);
        Shipment shipment = orderItem.getShipments().stream().toList().getLast();
        int countOrderItem = 0;
        for (OrderItem item : shipment.getOrderItems()) {
            if (!item.getOrderItemStatus().equals(OrderItemStatus.CANCELLED)) {
                countOrderItem++;
            }
        }
        if (countOrderItem == 0) {
            shipmentService.update(UpdateShipmentRequest.builder()
                    .shippingStatus(ShippingStatus.CANCELLED)
                    .shipmentId(shipment.getShipmentId())
                    .build(), null);
        }
        // update quanity list seller
        ListSeller listSeller = orderItem.getListSeller();
        listSeller.setQuantity(listSeller.getQuantity() + orderItem.getQuantity());
        listSellerRepo.save(listSeller);
        // hoan tien
        transactionService.releasePrice(orderItem);
        Order order = orderItem.getOrder();
        int countCancelled = 0;
        for (OrderItem item : order.getOrderItemList()) {
            if (item.getOrderItemStatus().equals(OrderItemStatus.CANCELLED)) {
                countCancelled++;
            }
        }
        if (countCancelled == order.getOrderItemList().size()) {
            order.setStatus(OrderStatus.CANCELLED);
        } else if (countCancelled > 0) {
            order.setStatus(OrderStatus.PARTIAL_CANCELLED);
        }
        orderRepo.save(order);
        return orderItemMapper.entityToResponse(orderItemsRepo.save(orderItem));
    }
    @Override
    @Transactional
    public OrderCardResponse cancleOrder(UUID orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow(
                () -> new AppException(ErrorCode.ORDER_NOT_FOUND)
        );
        List<OrderItem> orderItems = new ArrayList<>(order.getOrderItemList());
        for (OrderItem orderItem : orderItems) {
            if(!orderItem.getOrderItemStatus().equals(OrderItemStatus.CANCELLED)) {
                cancleOrderItem(orderItem.getOrderItemId());
            }
        }
        return orderMapper.entityToResponse(orderRepo.save(order));
    }

    @Override
    public PageResponse<OrderItemResponse> getMyReturnOrderItem(ShippingStatus shippingStatus, int page, int size) {
        page = page - 1;
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepo.findByEmail(name);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        List<OrderItemResponse> result = new ArrayList<>();
        List<Shipment> shipments = shipmentRepo.findByShipmentStatus(shippingStatus);
        shipments.forEach(shipment -> {
            List<OrderItem> orderItems = new ArrayList<>();
            shipment.getOrderItems().forEach(orderItem -> {
                if (orderItem.getListSeller().getSeller().getUserId().equals(users.getUserId())
                        && orderItem.getReturnRequest() != null && orderItem.getReturnRequest().getStatus().equals(ReturnRequestStatus.PAID)) {
                    orderItems.add(orderItem);
                }
            });
            result.add(
                    OrderItemResponse.builder()
                            .shipmentResponse(shipmentMapper.entityToResponse(shipment))
                            .shipfee(shipment.getShipmentFee())
                            .orderDetailResponseList(orderItems.stream().map(orderItemMapper::entityToResponse).toList())
                            .build()
            );
        });
        int totalPages = result.size() / size;
        int from = page * size;
        int to = Math.min(((page + 1) * size), result.size());
        List<OrderItemResponse> orderItems = new ArrayList<>();
        if(from < to) {
            orderItems = result.stream().toList().subList(from, to);
        }
        PageResponse<OrderItemResponse> pageResponse = PageResponse.<OrderItemResponse>builder()
                .content(orderItems)
                .totalElements(result.size())
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .last(to == result.size())
                .build();
        return pageResponse;
    }
}
