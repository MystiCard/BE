package com.example.mysterycard.repository;

import com.example.mysterycard.entity.RateConfig;
import com.example.mysterycard.enums.Rarity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RateConfigRepo extends JpaRepository<RateConfig, UUID> {
    boolean existsByCardRarity(Rarity cardRarity);
}
