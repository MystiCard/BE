package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Wallet;
import com.example.mysterycard.mapper.WalletMapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletRepo extends JpaRepository<Wallet, UUID> {
}
