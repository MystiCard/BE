package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Order;
import com.example.mysterycard.entity.OrderItem;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.enums.OrderItemStatus;
import com.example.mysterycard.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemsRepo extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByListSeller_Seller(Users listSellerSeller);


    Page<OrderItem> findByListSeller_SellerAndOrderItemStatusAndOrder_Status(Users listSellerSeller, OrderItemStatus orderItemStatus, OrderStatus orderStatus,Pageable pageable);

    Page<OrderItem> findByListSeller_SellerAndOrderItemStatusAndOrder_StatusNot(Users listSellerSeller, OrderItemStatus orderItemStatus, OrderStatus orderStatus, Pageable pageable);

    List<OrderItem> findByOrderItemStatusAndOrder_Status(OrderItemStatus orderItemStatus, OrderStatus orderStatus);

    List<OrderItem> findByOrderItemStatusAndOrder_StatusNot(OrderItemStatus orderItemStatus, OrderStatus orderStatus);
}
