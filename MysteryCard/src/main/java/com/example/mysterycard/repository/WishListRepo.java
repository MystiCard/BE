package com.example.mysterycard.repository;

import com.example.mysterycard.entity.WishList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WishListRepo extends JpaRepository<WishList, UUID> {
    Page<WishList> findByUser_UserId(UUID userId, Pageable pageable);
}
