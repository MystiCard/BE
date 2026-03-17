package com.example.mysterycard.repository;

import com.example.mysterycard.entity.BlindBox;
import com.example.mysterycard.enums.BlindBoxStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface BlindBoxRepo extends JpaRepository<BlindBox, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) // Khóa để ghi
    @Query("SELECT b FROM BlindBox b WHERE b.blindBoxId = :id")
    Optional<BlindBox> findByIdWithLock(UUID id);
    Page<BlindBox> findAllByBlindBoxStatus(BlindBoxStatus blindBoxStatus, Pageable pageable);
}
