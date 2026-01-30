package com.example.mysterycard.repository;

import com.example.mysterycard.entity.BlindBoxResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BlindBoxCardResultRepo extends JpaRepository<BlindBoxResult, UUID> {
}
