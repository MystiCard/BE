package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.response.CardResponse;
import com.example.mysterycard.dto.response.CategoryResponse;
import com.example.mysterycard.entity.Card;
import com.example.mysterycard.entity.Category;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.CardMapper;
import com.example.mysterycard.mapper.CategoryMapper;
import com.example.mysterycard.repository.CategoryRepo;
import com.example.mysterycard.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CardMapper cardMapper;

    @Override
    public CategoryResponse getCateById(UUID id) {
        Category cate = categoryRepo.findById(id) .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return categoryMapper.toResponse(cate);
    }

    @Override
    public List<CategoryResponse> getCategories() {
        List<Category> cates = categoryRepo.findAll();
        return cates.stream().map(categoryMapper::toResponse).toList();
    }

    @Override
    public CategoryResponse createCate(String categoryName) {
        if(categoryRepo.existsCategoryByCategoryName(categoryName)){
            throw new AppException(ErrorCode.CATEGORY_DUPLICATE);
        }
        Category cate = categoryRepo.save( Category.builder().categoryName(categoryName).build() );
        return categoryMapper.toResponse(cate);
    }

    @Override
    public void deleteCate(UUID id) {
        Category cate = categoryRepo.findById(id) .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryRepo.delete(cate);
    }

    @Override
    public CategoryResponse updateCate(UUID id, String categoryName) {
        Category cate = categoryRepo.findById(id) .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        cate.setCategoryName(categoryName);
        categoryRepo.save(cate);
        return categoryMapper.toResponse(cate);
    }

    @Override
    public List<CardResponse> getCardsByCategoryId(UUID id) {
        Category cate = categoryRepo.findById(id) .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        List<Card> cards = cate.getCardlist();
        return cards.stream().map(cardMapper::toResponse).toList();
    }


}
