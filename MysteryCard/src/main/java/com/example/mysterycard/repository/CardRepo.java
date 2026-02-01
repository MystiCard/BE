package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Card;
import com.example.mysterycard.entity.Category;
import com.example.mysterycard.enums.Rarity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CardRepo extends JpaRepository<Card, UUID> {
    boolean existsCardByNameAndRarityAndCategory(String name, Rarity rarity, Category category);

    List<Card> findByCategory_CategoryId(UUID categoryCategoryId);
}
