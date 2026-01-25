package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepo extends JpaRepository<Image, UUID> {
}
