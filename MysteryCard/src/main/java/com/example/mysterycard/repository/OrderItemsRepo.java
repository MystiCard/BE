package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Order;
import com.example.mysterycard.entity.OrderItem;
import com.example.mysterycard.entity.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemsRepo extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByListSeller_Seller(Users listSellerSeller);
}
