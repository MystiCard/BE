package com.example.mysterycard.service;

import com.example.mysterycard.dto.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse getCateById(UUID id);
    List<CategoryResponse> getCategories();
    CategoryResponse createCate(String categoryName);
    void deleteCate(UUID id);
    CategoryResponse updateCate(UUID id, String categoryName);
}
