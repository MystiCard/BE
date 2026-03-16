package com.example.mysterycard.repository;

import com.example.mysterycard.entity.BlindBox;
import com.example.mysterycard.entity.BlindBoxResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BlindBoxResultRepo extends JpaRepository<BlindBoxResult , UUID> {
    Optional<BlindBoxResult> findByBlindBoxResultId(UUID uuid);

}
