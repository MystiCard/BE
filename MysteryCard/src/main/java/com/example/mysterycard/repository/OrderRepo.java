package com.example.mysterycard.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.mysterycard.dto.response.OrderResponse;
import com.example.mysterycard.entity.Card;
import com.example.mysterycard.entity.Order;
import com.example.mysterycard.entity.OrderItem;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OrderRepo extends JpaRepository<Order, UUID>,  JpaSpecificationExecutor<Order>  {
    @Query("""
   select  o from Order  o where  o.buyer  = :users and o.blindBoxResults is not empty 
""")
    List<Order> getOrderHaveShipCard(Users users);
}
