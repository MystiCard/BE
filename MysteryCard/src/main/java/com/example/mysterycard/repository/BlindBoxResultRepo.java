package com.example.mysterycard.repository;

import com.example.mysterycard.entity.BlindBoxResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BlindBoxResultRepo extends JpaRepository<BlindBoxResult , UUID> {
    Optional<BlindBoxResult> findByBlindBoxResultId(UUID uuid);
}
