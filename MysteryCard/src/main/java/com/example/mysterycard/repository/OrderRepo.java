package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Order;
import com.example.mysterycard.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepo extends JpaRepository<Order, UUID> {

}
