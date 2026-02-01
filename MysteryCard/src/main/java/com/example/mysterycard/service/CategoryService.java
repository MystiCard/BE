package com.example.mysterycard.service;

import com.example.mysterycard.dto.response.CardResponse;
import com.example.mysterycard.dto.response.CategoryResponse;
import com.example.mysterycard.entity.Card;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse getCateById(UUID id);
    List<CategoryResponse> getCategories();
    CategoryResponse createCate(String categoryName);
    void deleteCate(UUID id);
    CategoryResponse updateCate(UUID id, String categoryName);
    List<CardResponse> getCardsByCategoryId(UUID id);
    Map<String, List<String>> importCategories(MultipartFile file);
    List<UUID> getCardIdsByCategoryId(UUID id);
}
