package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.CalculateFeeRequest;
import com.example.mysterycard.dto.request.OrderCardRequest;
import com.example.mysterycard.dto.response.OrderCardResponse;
import com.example.mysterycard.dto.response.OrderItemResponse;
import com.example.mysterycard.entity.ListSeller;
import com.example.mysterycard.entity.Order;
import com.example.mysterycard.entity.OrderItem;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.OrderItemMapper;
import com.example.mysterycard.mapper.OrderMapper;
import com.example.mysterycard.repository.ListSellerRepo;
import com.example.mysterycard.repository.OrderItemsRepo;
import com.example.mysterycard.repository.OrderRepo;
import com.example.mysterycard.repository.UsersRepo;
import com.example.mysterycard.service.OrderService;
import com.example.mysterycard.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderCardResponse createOrder(OrderCardRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepo.findByEmail(email);
        if(users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Order order = Order.builder()
                .buyer(users)
                .build();
        order = orderRepo.save(order);
        order.setOrderItemList(new ArrayList<>());
        double totalAmount = 0 ;
        List<OrderItemResponse> allOrderItemsResponse = new ArrayList<>();
        if(request.getOrderItemsList() != null && !request.getOrderItemsList().isEmpty()) {

            Map<UUID, List<OrderCardRequest.OrderItems>> groupedBySeller = request.getOrderItemsList().stream()
                    .collect(Collectors.groupingBy(item -> {
                        ListSeller ls = listSellerRepo.findById(item.getListSellerId())
                                .orElseThrow(() -> new AppException(ErrorCode.LIST_SELLER_NOT_FOUND));
                        return ls.getSeller().getUserId();
                    }));


            for (Map.Entry<UUID,List<OrderCardRequest.OrderItems>>  orderItems : groupedBySeller.entrySet()) {
                List<OrderItem> orderItemList = new ArrayList<>();
                ListSeller listSeller = listSellerRepo.findById(orderItems.getValue().getFirst().getListSellerId())
                        .orElseThrow(() -> new AppException(ErrorCode.LIST_SELLER_NOT_FOUND));
                double totalOrderItems =0L;

                for(OrderCardRequest.OrderItems item : orderItems.getValue()) {
                    log.info("Item {}",item.toString());
                    ListSeller ls = listSellerRepo.findById(item.getListSellerId())
                            .orElseThrow(() -> new AppException(ErrorCode.LIST_SELLER_NOT_FOUND));
                    if (item.getQuantity() > ls.getQuantity() ) {
                        throw  new AppException(ErrorCode.QUANTITY_OVER_AVAIABLE);
                    }

                    OrderItem orderIt = OrderItem.builder()
                            .quantity(item.getQuantity())
                            .listSeller(ls)
                            .order(order)
                            .price(ls.getPrice())
                            .build();
                    order.getOrderItemList().add(orderIt);
                    orderItemList.add( orderItemsRepo.save(orderIt));
                    double price = orderIt.getQuantity() * ls.getPrice();
                    totalOrderItems+=price;
                    totalAmount+= price;

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
totalAmount+=shipfee;
                OrderItemResponse orderItemResponse  = OrderItemResponse.builder()
                        .orderDetailResponseList(orderItemList.stream().map(orderItemMapper::entityToResponse).toList())
                        .shipfee(shipfee)
                        .build();
                allOrderItemsResponse.add(orderItemResponse);
                }

            }
        order.setTotalAmount(totalAmount);
        OrderCardResponse orderCardResponse = orderMapper.entityToResponse(orderRepo.save(order));
        orderCardResponse.setOrderItems(allOrderItemsResponse);
        return orderCardResponse;
    }
}
