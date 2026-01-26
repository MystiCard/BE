package com.example.mysterycard.repository;

import com.example.mysterycard.entity.BlindBox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BlindBoxRepo extends JpaRepository<BlindBox, Long> {

}
