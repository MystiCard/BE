package com.example.mysterycard.repository;

import com.example.mysterycard.entity.BlindBoxResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BlindBoxCardResultRepo extends JpaRepository<BlindBoxResult, UUID> {
    Page<BlindBoxResult> findByOwner_UserId(UUID ownerUserId, Pageable pageable);
}
