package com.example.mysterycard.repository;

import com.example.mysterycard.entity.ListSeller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ListSellerRepo extends JpaRepository<ListSeller, UUID> {
    Page<ListSeller> findByCard_CardId(UUID cardId, Pageable pageable);
}
