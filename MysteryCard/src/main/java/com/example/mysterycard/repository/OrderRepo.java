package com.example.mysterycard.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.mysterycard.dto.response.OrderResponse;
import com.example.mysterycard.entity.Card;
import com.example.mysterycard.entity.Order;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface OrderRepo extends JpaRepository<Order, UUID>,  JpaSpecificationExecutor<Order>  {
    Page<Order> findByBuyer(Users buyer, Pageable pageable, Specification<Order> orderSpecification);
}
