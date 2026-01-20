package com.example.mysterycard.repository;

import com.example.mysterycard.entity.RefreshToken;
import com.example.mysterycard.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepo  extends JpaRepository<RefreshToken, Long> {

    RefreshToken findByToken(String token);

    Iterable<? extends RefreshToken> findAllByUser(Users user);
}
