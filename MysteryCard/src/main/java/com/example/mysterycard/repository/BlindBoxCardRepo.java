package com.example.mysterycard.repository;

import com.example.mysterycard.entity.BlindBox;
import com.example.mysterycard.entity.BlindBoxCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BlindBoxCardRepo extends JpaRepository<BlindBoxCard, UUID> {
    List<BlindBoxCard> findAllByBlindBoxAndStatusTrue(BlindBox box);
}
