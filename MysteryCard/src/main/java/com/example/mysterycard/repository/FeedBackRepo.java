package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Feedback;
import com.example.mysterycard.entity.OrderItem;
import com.example.mysterycard.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.UUID;

public interface FeedBackRepo extends JpaRepository<Feedback, UUID> {


    Page<Feedback> findByOrderItemIsIn(Collection<OrderItem> orderItems, Pageable pageable);

    boolean existsByOrderItemAndBuyer(OrderItem orderItem, Users buyer);
    Page<Feedback> findBySeller(Users seller, Pageable pageable);
}
