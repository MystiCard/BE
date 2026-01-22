package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepo extends JpaRepository<Category , UUID> {
        boolean existsCategoryByCategoryName(String categoryName);
}
