package com.example.mysterycard.repository;

import com.example.mysterycard.dto.response.TrackingResponse;
import com.example.mysterycard.entity.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrackingRepo extends JpaRepository<Tracking, UUID> {
}
