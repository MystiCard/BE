package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartRepo extends JpaRepository<Cart, UUID> {
}
