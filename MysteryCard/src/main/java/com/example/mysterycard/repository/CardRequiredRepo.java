package com.example.mysterycard.repository;

import com.example.mysterycard.entity.CardRequired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardRequiredRepo extends JpaRepository<CardRequired, UUID> {
    Page<CardRequired> findByUsers_userId(UUID UserId, Pageable pageable);

    Optional<CardRequired> findByCardRequiredId(UUID cardRequiredId);
    Page<CardRequired> findAll(Pageable pageable);
}
