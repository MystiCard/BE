package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.CalculateFeeRequest;
import com.example.mysterycard.dto.request.OrderCardRequest;
import com.example.mysterycard.dto.response.OrderCardResponse;
import com.example.mysterycard.entity.ListSeller;
import com.example.mysterycard.entity.Order;
import com.example.mysterycard.entity.OrderItem;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.OrderMapper;
import com.example.mysterycard.repository.ListSellerRepo;
import com.example.mysterycard.repository.OrderItemsRepo;
import com.example.mysterycard.repository.OrderRepo;
import com.example.mysterycard.repository.UsersRepo;
import com.example.mysterycard.service.OrderService;
import com.example.mysterycard.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepo orderRepo;
    private final OrderItemsRepo orderItemsRepo;
    private final ShipmentService shipmentService;
    private final UsersRepo usersRepo;
    private final ListSellerRepo listSellerRepo;

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
        double totalAmount = 0 ;
        if(request.getOrderItemsList() != null && !request.getOrderItemsList().isEmpty()) {
            for (OrderCardRequest.OrderItems orderItem : request.getOrderItemsList()) {
                ListSeller listSeller = listSellerRepo.findById(orderItem.getListSellerId()).orElseThrow(
                        ()-> new AppException(ErrorCode.LIST_SELLER_NOT_FOUND)
                );
                OrderItem orderItems = OrderItem.builder()
                        .quantity(orderItem.getQuantity())
                        .listSeller(listSeller)
                        .order(order)
                        .price(listSeller.getPrice())
                        .build();
                orderItemsRepo.save(orderItems);
                totalAmount+= (orderItems.getQuantity() * listSeller.getPrice());
             }
            }
        order.setTotalAmount(totalAmount);

        OrderCardResponse orderCardResponse = orderMapper.entityToResponse(orderRepo.save(order));
        orderCardResponse.setShipfee(shipmentService.calculatFeeShip(
                CalculateFeeRequest.builder()
                        .totalAmount(order.getTotalAmount())
                        .fromDistrictId(request.getFromDistrictId())
                        .toWardId(request.getToWardId())
                        .toDistrictId(request.getToDistrictId())
                        .build()
        ));
        return orderCardResponse;
    }
}
