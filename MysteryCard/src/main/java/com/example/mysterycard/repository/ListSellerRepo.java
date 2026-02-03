package com.example.mysterycard.repository;

import com.example.mysterycard.entity.ListSeller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ListSellerRepo extends JpaRepository<ListSeller, UUID> {

}
