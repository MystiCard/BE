package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardRepo extends JpaRepository<Card, UUID> {
    boolean existsCardByName(String name);
}
