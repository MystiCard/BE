package com.example.mysterycard.repository;

import com.example.mysterycard.entity.BlindBoxPurChase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BlindBoxPurchaseRepo extends JpaRepository<BlindBoxPurChase, UUID> {
}
