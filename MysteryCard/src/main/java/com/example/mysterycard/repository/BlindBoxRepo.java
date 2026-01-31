package com.example.mysterycard.repository;

import com.example.mysterycard.entity.BlindBox;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface BlindBoxRepo extends JpaRepository<BlindBox, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) // Khóa để ghi
    @Query("SELECT b FROM BlindBox b WHERE b.blindBoxId = :id")
    Optional<BlindBox> findByIdWithLock(Long id);
}
