package com.example.mysterycard.repository;

import com.example.mysterycard.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WishListRepo extends JpaRepository<WishList, UUID> {
}
